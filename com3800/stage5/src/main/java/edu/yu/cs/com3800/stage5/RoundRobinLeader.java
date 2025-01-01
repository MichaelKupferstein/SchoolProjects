package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import edu.yu.cs.com3800.Util;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;

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
    private Map<Long, WorkData> pendingRequests;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private AtomicLong nextWorkerID;
    private ConcurrentHashMap<Long, Message> completedWork;

    public RoundRobinLeader(PeerServer myServer, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages) throws IOException {
        this.myServer = myServer;
        this.incomingMessages = incomingMessages;
        this.workers = new HashMap<>(peerIDtoAddress);
        this.workers.remove(myServer.getServerId());
        this.workerIterator = workers.entrySet().iterator();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.serverSocket = new ServerSocket(myServer.getUdpPort()+2);
        this.executorService = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors())*2);
        this.nextWorkerID = new AtomicLong(1);
        this.completedWork = new ConcurrentHashMap<>();
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
            }finally {
                shutdown();
            }
        }
        logger.info("RRL shutting down");
    }

    private void handleClienRequest(Socket clientSocket){
        Socket workerSocket = null;
        try {
            byte[] reqData = Util.readAllBytesFromNetwork(clientSocket.getInputStream());
            Message req = new Message(reqData);

            Message completedResponse = completedWork.get(req.getRequestID());
            if (completedResponse != null) {
                sendResponse(clientSocket, completedResponse);
                return;
            }

            Map.Entry<Long, InetSocketAddress> worker = getNextWorker();
            if(worker == null){
                logger.warning("No workers available");
                return;
            }

            long reqeustId = nextWorkerID.getAndIncrement();
            assignWork(worker,req,reqeustId,clientSocket);
        }catch(IOException e){
            handleRequestError(clientSocket, e);
        }
    }

    private void handleRequestError(Socket clientSocket, Exception e){
        try{
            Message error = new Message(COMPLETED_WORK,e.getMessage().getBytes(),myServer.getAddress().getHostString(),myServer.getUdpPort(),
                    clientSocket.getInetAddress().getHostAddress(),clientSocket.getPort(),-1,true);
            sendResponse(clientSocket,error);
        }catch(IOException e2){
            logger.severe("Error sending error response: " + e2.getMessage());
        }
    }

    private void assignWork(Map.Entry<Long,InetSocketAddress> worker, Message req, long requestId, Socket clientSocket){
        try{
            InetSocketAddress workerAddress = worker.getValue();
            int workerPort = workerAddress.getPort() + 2;

            Message workMessage = new Message(Message.MessageType.WORK,req.getMessageContents(),myServer.getAddress().getHostString(),myServer.getUdpPort(),
                    workerAddress.getHostString(),workerPort,requestId);

            pendingRequests.put(requestId,new WorkData(worker.getKey(),req));

            Socket workerSocket = new Socket(workerAddress.getAddress(),workerPort);
            OutputStream out = workerSocket.getOutputStream();
            out.write(workMessage.getNetworkPayload());
            out.flush();

            byte[] response = Util.readAllBytesFromNetwork(workerSocket.getInputStream());
            Message responseMsg = new Message(response);
            completedWork.put(requestId,responseMsg);
            pendingRequests.remove(requestId);
            sendResponse(clientSocket,responseMsg);
            workerSocket.close();

        }catch(IOException e){
            logger.severe("Error sending work to worker" +  worker.getKey() + ": " + e.getMessage());
            reassignWork(requestId);
            handleRequestError(clientSocket,e);
        }
    }

    private void reassignWork(long requestId){
        WorkData work = pendingRequests.get(requestId);
        if(work != null){
            pendingRequests.remove(requestId);

            Map.Entry<Long,InetSocketAddress> worker = getNextWorker();
            if(worker != null && !worker.getKey().equals(work.workderId)){
                try{
                    assignWork(worker,work.request,requestId,null);
                }catch (Exception e) {
                    logger.severe("Error reassigning work " + requestId + ": " + e.getMessage());
                }
            }
        }
    }

    private Map.Entry<Long,InetSocketAddress> getNextWorker(){
        if(!workerIterator.hasNext()){
            workerIterator = workers.entrySet().iterator();
        }
        while(workerIterator.hasNext()){
            Map.Entry<Long,InetSocketAddress> worker = workerIterator.next();
            if(!myServer.isPeerDead(worker.getKey())){
                return worker;
            }
        }
        return null;
    }

    private void sendResponse(Socket clientSocket, Message response) throws IOException{
        if(clientSocket != null && !clientSocket.isClosed()){
            OutputStream out = clientSocket.getOutputStream();
            out.write(response.getNetworkPayload());
            out.flush();
        }
    }

    public void handleWorkerFailure(long workerId){
        for(Map.Entry<Long,WorkData> entry : pendingRequests.entrySet()){
            if(entry.getValue().workderId == workerId){
                reassignWork(entry.getKey());
            }
        }
    }

    public Map<Long, Message> getCompletedWork(){
        return new HashMap<>(completedWork);
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

    private static class WorkData{
        final long workderId;
        final Message request;
        final long startTime;

        public WorkData(long workerId, Message request){
            this.workderId = workerId;
            this.request = request;
            this.startTime = System.currentTimeMillis();
        }
    }
}