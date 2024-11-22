package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;
import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;
import static edu.yu.cs.com3800.Message.MessageType.WORK;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
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
    private Map<Long,Message> clientRequests;
    private long nextWorkerID = 1;

    public RoundRobinLeader(PeerServer myServer, Map<Long, InetSocketAddress> peerIDtoAddress, LinkedBlockingQueue<Message> incomingMessages) throws IOException {
        this.myServer = myServer;
        this.incomingMessages = incomingMessages;
        this.workers = new HashMap<>(peerIDtoAddress);
        this.workers.remove(myServer.getServerId());
        this.workerIterator = workers.entrySet().iterator();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.clientRequests = new ConcurrentHashMap<>();
        this.logger = initializeLogging(RoundRobinLeader.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        setDaemon(true);
        logger.fine("RoundRobinLeader initialized on port " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    @Override
    public void run(){
        while(!shutdown){
            try{
                Message message = this.incomingMessages.take();
                processMessage(message);
            }catch (InterruptedException e){
                if(shutdown){
                    break;
                }
            }
        }
        logger.info("RRL shutting down");
    }

    public void processMessage(Message message){
        if(message.getMessageType() == WORK) giveWork(message);
        else if( message.getMessageType() == COMPLETED_WORK) processCompletedWork(message);
    }

    private void giveWork(Message message) {
        if(!workerIterator.hasNext()) workerIterator = workers.entrySet().iterator();

        if(workerIterator.hasNext()){
            Map.Entry<Long,InetSocketAddress> worker = workerIterator.next();

            long curId = nextWorkerID++;

            clientRequests.put(curId, message);

            Message workMessage = new Message(WORK, message.getMessageContents(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), worker.getValue().getHostString(), worker.getValue().getPort(), curId);
            pendingRequests.put(curId, message);

            myServer.sendMessage(workMessage.getMessageType(), workMessage.getNetworkPayload(), worker.getValue());
        }
    }

    private void processCompletedWork(Message message) {
        Message originalWork = pendingRequests.remove(message.getRequestID());
        if(originalWork != null){
            Message response = new Message(COMPLETED_WORK, message.getMessageContents(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), originalWork.getSenderHost(), originalWork.getSenderPort(), originalWork.getRequestID());
            myServer.sendMessage(response.getMessageType(), response.getNetworkPayload(), new InetSocketAddress(originalWork.getSenderHost(), originalWork.getSenderPort()));
            clientRequests.remove(originalWork.getRequestID());
        }
    }


    public void shutdown() {
        this.shutdown = true;
        interrupt();
    }
}
