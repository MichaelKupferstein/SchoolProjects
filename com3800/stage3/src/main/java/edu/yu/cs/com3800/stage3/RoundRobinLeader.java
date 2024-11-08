package edu.yu.cs.com3800.stage3;

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
import java.util.logging.Logger;


public class RoundRobinLeader extends Thread implements LoggingServer {
    private PeerServer myServer;
    private volatile boolean shutdown;
    private static Logger logger;

    private Map<Long, InetSocketAddress> workers;
    private Iterator<Map.Entry<Long,InetSocketAddress>> workerIterator;
    private int currentWorkerIndex = 0;
    private Map<Long, Message> pendingRequests;
    private long nextWorkerID = 1;

    public RoundRobinLeader(PeerServer myServer, Map<Long, InetSocketAddress> peerIDtoAddress) throws IOException {
        this.myServer = myServer;
        this.workers = new HashMap<>(peerIDtoAddress);
        this.workers.remove(myServer.getServerId());
        this.workerIterator = workers.entrySet().iterator();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.logger = initializeLogging(RoundRobinLeader.class.getCanonicalName() + "-on-port-" + this.myServer.getUdpPort());
        setDaemon(true);
        logger.fine("RoundRobinLeader initialized on port " + myServer.getUdpPort() + " from PeerServer: " + myServer.getServerId());
    }

    public void processMessage(Message message){
        if(message.getMessageType() == WORK) giveWork(message);
        else if( message.getMessageType() == COMPLETED_WORK) processCompletedWork(message);
    }
    private void giveWork(Message message) {
        if(!workerIterator.hasNext()) workerIterator = workers.entrySet().iterator();

        if(workerIterator.hasNext()){
            Map.Entry<Long,InetSocketAddress> worker = workerIterator.next();
            Message workMessage = new Message(WORK, message.getMessageContents(), myServer.getAddress().getHostString(),
                    myServer.getUdpPort(), worker.getValue().getHostString(), worker.getValue().getPort(), nextWorkerID);
            pendingRequests.put(nextWorkerID, workMessage);
            nextWorkerID++;
            myServer.sendMessage(workMessage.getMessageType(), workMessage.getNetworkPayload(), worker.getValue());
        }
    }

    private void processCompletedWork(Message message) {
        Message originalWork = pendingRequests.remove(message.getRequestID());
        if(originalWork != null){
            myServer.sendMessage(COMPLETED_WORK, message.getMessageContents(), new InetSocketAddress(originalWork.getSenderHost(), originalWork.getSenderPort()));
        }
    }




    public void shutdown() {
        this.shutdown = true;
        interrupt();
    }
}
