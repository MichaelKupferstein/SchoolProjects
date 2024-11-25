package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import edu.yu.cs.com3800.Util;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;
import static edu.yu.cs.com3800.Message.MessageType.WORK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;


public class RoundRobinLeader extends Thread implements LoggingServer {
    private PeerServer myServer;
    private volatile boolean shutdown;
    private static Logger logger;
    private LinkedBlockingQueue<Message> incomingMessages;

    private Map<Long, InetSocketAddress> workers;
    private Iterator<Map.Entry<Long,InetSocketAddress>> workerIterator;
    private int currentWorkerIndex = 0;
    private Map<Long, Message> pendingRequests;
    private long nextWorkerID = 1;

    private ServerSocket serverSocket;
    private ExecutorService requestPool;

    public RoundRobinLeader(PeerServer myServer, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages) throws IOException {
        this.myServer = myServer;
        this.incomingMessages = incomingMessages;
        this.workers = new HashMap<>(peerIDtoAddress);
        this.workers.remove(myServer.getServerId());
        this.workerIterator = workers.entrySet().iterator();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.logger = initializeLogging(RoundRobinLeader.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        this.serverSocket = new ServerSocket(myServer.getUdpPort()+2);
        this.requestPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        setDaemon(true);
        setName("RoundRobinLeader-port-" + this.myServer.getUdpPort());
        logger.fine("RoundRobinLeader initialized on port " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    @Override
    public void run(){

        CompletableFuture.runAsync(this::acceptConnections, requestPool);

        while(!shutdown && !isInterrupted()){
            try{
                Message message = this.incomingMessages.take();
                if(message.getMessageType() == COMPLETED_WORK){
                    processCompletedWork(message);
                }
            }catch (InterruptedException e){
                if(shutdown){
                    break;
                }
            }
        }
        requestPool.shutdownNow();
        try{
            serverSocket.close();
        }catch (IOException e){
            logger.severe("Error closing server socket: " + e.getMessage());
        }
        logger.info("RRL shutting down");
    }

    private void acceptConnections(){
        while(!shutdown && !isInterrupted()){
            try{
                Socket socket = serverSocket.accept();
                requestPool.submit(() -> handleClientConnection(socket));
            }catch (IOException e){
                if(shutdown){
                    break;
                }
            }

        }
    }

    private void handleClientConnection(Socket socket){
        try{
            InputStream in = socket.getInputStream();
            byte[] msgBytes = Util.readAllBytesFromNetwork(in);
            Message message = new Message(msgBytes);

            if(message.getMessageType() == WORK){
                byte[] res = giveWork(message);

                try(OutputStream out = socket.getOutputStream()){
                    out.write(res);
                    out.flush();
                }
            }
        }catch (IOException e){
            logger.severe("Error handling client connection: " + e.getMessage());
        }finally {
            try{
                socket.close();
            }catch (IOException e){
                logger.severe("Error closing client connection: " + e.getMessage());
            }
        }

    }

    public void processMessage(Message message){
        if(message.getMessageType() == WORK) giveWork(message);
        else if( message.getMessageType() == COMPLETED_WORK) processCompletedWork(message);
    }

    private byte[] giveWork(Message message) {
        if(!workerIterator.hasNext()) {
            workerIterator = workers.entrySet().iterator();
        }

        Map.Entry<Long, InetSocketAddress> worker = workerIterator.next();
        InetSocketAddress workerAddress = worker.getValue();
        String workerHost = workerAddress.getHostString();
        int workerPort = workerAddress.getPort() + 2;

        try(Socket workerSocket = new Socket(workerHost, workerPort)) {
            Message msg = new Message(WORK, message.getMessageContents(), myServer.getAddress().getHostString(), myServer.getUdpPort(), workerHost, workerPort, message.getRequestID());
            OutputStream os = workerSocket.getOutputStream();
            os.write(msg.getNetworkPayload());
            os.flush();

            InputStream is = workerSocket.getInputStream();
            byte[] res = Util.readAllBytesFromNetwork(is);

            return res;
        }catch (IOException e){
            logger.severe("Error giving work to worker: " + e.getMessage());
            String error = "Error giving work to worker: " + e.getMessage();
            return error.getBytes();
        }

    }

    private void processCompletedWork(Message message) {
        Message originalWork = pendingRequests.remove(message.getRequestID());
        if(originalWork != null){
            Message response = new Message(COMPLETED_WORK, message.getMessageContents(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), originalWork.getSenderHost(), originalWork.getSenderPort(), originalWork.getRequestID());
            myServer.sendMessage(response.getMessageType(), response.getNetworkPayload(), new InetSocketAddress(originalWork.getSenderHost(), originalWork.getSenderPort()));
        }
    }


    public void shutdown() {
        this.shutdown = true;
        interrupt();
    }
}
