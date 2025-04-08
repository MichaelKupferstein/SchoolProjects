package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaRunnerFollower extends Thread implements LoggingServer {
    private PeerServer myServer;
    private JavaRunner javaRunner;
    private volatile boolean shutdown;
    private static Logger logger;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Long, Message> completedWork;


    public JavaRunnerFollower(PeerServer myServer) throws IOException {
        this.myServer = myServer;
        this.javaRunner = new JavaRunner();
        this.serverSocket = new ServerSocket(myServer.getUdpPort()+2);
        this.logger = initializeLogging(JavaRunnerFollower.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        this.completedWork = new ConcurrentHashMap<>();
        setDaemon(true);
        logger.fine("JavaRunnerFollower initialized on port: " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    @Override
    public void run(){
        while(!shutdown){
            try{
                Socket leaderSocket = serverSocket.accept();
                work(leaderSocket);
            }catch(IOException e){
                if(!shutdown){
                    logger.severe("Error accepting leader connection: " + e.getMessage());
                }
            }
        }
        logger.info("JavaFOllower shutting down");
    }


    public void shutdown(){
        this.shutdown = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.severe("Error closing server socket: " + e.getMessage());
        }
        interrupt();
    }

    public void work(Socket leaderSocket){
        try {
           byte[] reqData = Util.readAllBytesFromNetwork(leaderSocket.getInputStream());
           Message workMsg = new Message(reqData);

           byte[] res;
           boolean error = false;
           try{
               String output = javaRunner.compileAndRun(new ByteArrayInputStream(workMsg.getMessageContents()));
                res = output.getBytes();
           }catch (Exception e){
               res = e.getMessage().getBytes();
               error = true;
           }

           Message response = new Message(COMPLETED_WORK, res, myServer.getAddress().getHostString(), myServer.getUdpPort(),
                      workMsg.getSenderHost(), workMsg.getSenderPort(), workMsg.getRequestID(), error);

           completedWork.put(workMsg.getRequestID(), response);

           if(myServer.getCurrentLeader() != null){
               OutputStream out = leaderSocket.getOutputStream();
               out.write(response.getNetworkPayload());
               out.flush();
           }

        }catch (IOException e){
            logger.severe("Error handling work request: " + e.getMessage());
        }finally {
            try {
                if(leaderSocket != null && !leaderSocket.isClosed()){
                    leaderSocket.close();
                }
            } catch (IOException e) {
                logger.severe("Error closing leader socket: " + e.getMessage());
            }
        }
    }

    public Map<Long,Message> getCompletedWork(){
        return new HashMap<>(completedWork);
    }

    public void clearCompletedWork(){
        completedWork.clear();
    }

}