package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.*;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;
import static edu.yu.cs.com3800.Message.MessageType.WORK;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class JavaRunnerFollower extends Thread implements LoggingServer {
    private PeerServer myServer;
    private JavaRunner javaRunner;
    private volatile boolean shutdown;
    private static Logger logger;
    private LinkedBlockingQueue<Message> incomingMessages;
    private ServerSocket serverSocket;


    public JavaRunnerFollower(PeerServer myServer, LinkedBlockingQueue<Message> incomingMessages) throws IOException {
        this.myServer = myServer;
        this.incomingMessages = incomingMessages;
        this.javaRunner = new JavaRunner();
        this.logger = initializeLogging(JavaRunnerFollower.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        this.serverSocket = new ServerSocket(myServer.getUdpPort()+2);
        setDaemon(true);
        setName("JavaRunnerFollower-port-" + this.myServer.getUdpPort());
        logger.fine("JavaRunnerFollower initialized on port: " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    @Override
    public void run(){
        while(!shutdown && !isInterrupted()){
            try{
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            }catch(IOException e){
                if(shutdown) break;
            }
        }
        logger.info("JavaFOllower shutting down");
    }

    private void handleRequest(Socket socket) {
        try {
            InputStream in = socket.getInputStream();
            byte[] messageBytes = Util.readAllBytesFromNetwork(in);
            Message msg = new Message(messageBytes);

            if(msg.getMessageType() != WORK){
                logger.severe("JavaRunnerFollower received a message that was not WORK");
                return;
            }

            byte[] response = processWork(msg);

            try(OutputStream out = socket.getOutputStream()){
                out.write(response);
                out.flush();
            }

        } catch (IOException e) {
            logger.severe("JavaRunnerFollower failed to handle request: " + e.getMessage());
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.severe("JavaRunnerFollower failed to close socket: " + e.getMessage());
            }
        }
    }

    private byte[] processWork(Message msg){
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(msg.getMessageContents());
            String res = javaRunner.compileAndRun(bais);
            logger.fine("JavaRunnerFollower compiled and ran code");
            return new Message(COMPLETED_WORK, res.getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), msg.getSenderHost(), msg.getSenderPort(), msg.getRequestID(),false).getNetworkPayload();
        } catch (Exception e) {
            logger.severe("JavaRunnerFollower failed to compile and run code");

            return new Message(COMPLETED_WORK,e.getMessage().getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), msg.getSenderHost(), msg.getSenderPort(), msg.getRequestID(),true).getNetworkPayload();
        }
    }


    public void shutdown(){
        this.shutdown = true;
        try{
            serverSocket.close();
        }catch (IOException e){
            logger.severe("JavaRunnerFollower failed to close server socket: " + e.getMessage());
        }
        interrupt();
    }

    public void work(Message message){
        logger.fine("JavaRunnerFollower received message");


        InetSocketAddress leaderAddress = myServer.getPeerByID(myServer.getCurrentLeader().getProposedLeaderID());
        long requestID = message.getRequestID();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(message.getMessageContents());
            String res = javaRunner.compileAndRun(bais);
            logger.fine("JavaRunnerFollower compiled and ran code");
            Message response = new Message(COMPLETED_WORK, res.getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), leaderAddress.getHostString(), leaderAddress.getPort(), message.getRequestID());
            myServer.sendMessage(response.getMessageType(), response.getNetworkPayload(), leaderAddress);
        } catch (Exception e) {
            logger.severe("JavaRunnerFollower failed to compile and run code");
            Message errorMessage = new Message(COMPLETED_WORK, e.getMessage().getBytes(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), leaderAddress.getHostString(), leaderAddress.getPort(), message.getRequestID());
            myServer.sendMessage(errorMessage.getMessageType(), errorMessage.getNetworkPayload(), leaderAddress);
        }
    }

}
