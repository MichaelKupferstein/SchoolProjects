package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import edu.yu.cs.com3800.Util;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;
import static edu.yu.cs.com3800.Message.MessageType.WORK;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;


public class RoundRobinLeader extends Thread implements LoggingServer {
    private PeerServer myServer;
    private volatile boolean shutdown;
    private static Logger logger;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Map<Long, InetSocketAddress> workers;
    private Iterator<Map.Entry<Long,InetSocketAddress>> workerIterator;
    private Map<Long, Message> pendingRequests;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private AtomicLong nextWorkerID = new AtomicLong(1);

    public RoundRobinLeader(PeerServer myServer, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages) throws IOException {
        this.myServer = myServer;
        this.incomingMessages = incomingMessages;
        this.workers = new HashMap<>(peerIDtoAddress);
        this.workers.remove(myServer.getServerId());
        this.workerIterator = workers.entrySet().iterator();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.serverSocket = new ServerSocket(myServer.getUdpPort()+2);
        this.executorService = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors())*2);
        this.logger = initializeLogging(RoundRobinLeader.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        setDaemon(true);
        logger.fine("RoundRobinLeader initialized on port " + (myServer.getUdpPort()+2) + " from PeerServer: " + myServer.getServerId());
    }

    @Override
    public void run(){
        while(!shutdown){
            try{
                Socket clientSocket = serverSocket.accept();
                executorService.submit(()->handleClienRequest(clientSocket));
            }catch (IOException e){
                if(!shutdown){
                    logger.severe("Error accepting client connection: " + e.getMessage());
                }
            }
        }
        logger.info("RRL shutting down");
    }

    private void handleClienRequest(Socket clientSocket){
        Socket workerSocket = null;
        try{
            byte[] reqData = Util.readAllBytesFromNetwork(clientSocket.getInputStream());
            Message req = new Message(reqData);

            if(!workerIterator.hasNext()){
                workerIterator = workers.entrySet().iterator();
            }
            Map.Entry<Long,InetSocketAddress> worker = workerIterator.next();

            InetSocketAddress workerAddress = worker.getValue();
            int workerPort = workerAddress.getPort() +2;
            workerSocket = new Socket(workerAddress.getAddress(), workerPort);

            long requestID = nextWorkerID.getAndIncrement();
            Message workMessage = new Message(WORK, req.getMessageContents(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), workerAddress.getHostString(), workerAddress.getPort(), requestID);
            pendingRequests.put(requestID, req);

            OutputStream out = workerSocket.getOutputStream();
            out.write(workMessage.getNetworkPayload());
            out.flush();

            byte[] response = Util.readAllBytesFromNetwork(workerSocket.getInputStream());
            Message responseMsg = new Message(response);

            OutputStream clientOut = clientSocket.getOutputStream();
            clientOut.write(responseMsg.getNetworkPayload());
            clientOut.flush();

            pendingRequests.remove(requestID);

        }catch (IOException e){
            logger.severe("Error handiling client request: " + e.getMessage());
            try{
                Message error = new Message(COMPLETED_WORK, e.getMessage().getBytes(), myServer.getAddress().getHostString(),
                        myServer.getUdpPort(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(), -1, true);
                OutputStream out = clientSocket.getOutputStream();
                out.write(error.getNetworkPayload());
                out.flush();
            }catch (IOException e2){
                logger.severe("Error sending error response to client: " + e2.getMessage());
            }
        }finally {
            try {
                if(clientSocket != null && !clientSocket.isClosed()){
                    clientSocket.close();
                }
                if(workerSocket != null && !workerSocket.isClosed()){
                    workerSocket.close();
                }
            } catch (IOException e) {
                logger.severe("Error closing client socket: " + e.getMessage());
            }
        }
    }


    public void shutdown() {
        this.shutdown = true;
        this.executorService.shutdown();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            logger.severe("Error closing server socket: " + e.getMessage());
        }
        interrupt();
    }
}