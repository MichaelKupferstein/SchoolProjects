package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class Gossiper extends Thread implements LoggingServer {

    private static final int GOSSIP = 3000;
    private static final int FAIL = GOSSIP * 10;
    private static final int CLEANUP = FAIL * 2;

    private PeerServerImpl myPeerServer;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private GossipData gossipData;
    private Map<Long, InetSocketAddress> peerIDtoAddress;
    private volatile boolean shutdown;
    private Logger logger;
    private Logger verboseLogger;

    public Gossiper(PeerServerImpl myPeerServer, LinkedBlockingQueue<Message> outgoingMessages,
                    LinkedBlockingQueue<Message> incomingMessages, Map<Long, InetSocketAddress> peerIDtoAddress) throws IOException {
        this.myPeerServer = myPeerServer;
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
        this.peerIDtoAddress = peerIDtoAddress;
        this.gossipData = new GossipData();
        setDaemon(true);
        setName("Gossiper-" + myPeerServer.getServerId());
        this.logger = initializeLogging(Gossiper.class.getCanonicalName() + "-on-port-" + this.myPeerServer.getUdpPort());
        this.verboseLogger = initializeLogging(Gossiper.class.getCanonicalName() + "-verbose-on-port-" + this.myPeerServer.getUdpPort(),true);
    }

    public static byte[] buildGossipMessage(GossipData gossipData) {
        Map<Long,Long> heartbeats = gossipData.getHeartbeats();
        Map<Long,Boolean> failedNodes = gossipData.getFailedNodes();

        int bufferSize = 8 + (heartbeats.size() * 24) + (failedNodes.size() * 9);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        buffer.putInt(heartbeats.size());
        heartbeats.forEach((nodeId, heartbeat) -> {
            buffer.putLong(nodeId);
            buffer.putLong(heartbeat);
            buffer.putLong(gossipData.getLastHeartbeatTime(nodeId));
        });
        buffer.putInt(failedNodes.size());
        failedNodes.keySet().forEach(buffer::putLong);

        return buffer.array();
    }

    public static void updateGossipDataFromMessage(byte[] content, GossipData gossipData) {
        ByteBuffer buffer = ByteBuffer.wrap(content);
        int numHeartbeats = buffer.getInt();
        for(int i = 0; i < numHeartbeats; i++) {
            long nodeId = buffer.getLong();
            long heartbeat = buffer.getLong();
            long timestamp = buffer.getLong();
            gossipData.updateFromGossip(nodeId, heartbeat, timestamp);
        }
        int numFailedNodes = buffer.getInt();
        for(int i = 0; i < numFailedNodes; i++) {
            long nodeId = buffer.getLong();
            gossipData.markNodeFailed(nodeId);
        }
    }

    @Override
    public void run() {
        try{
            while (!shutdown) {
                processIncomingMessages();
                sendGossip();
                checkFailedNodes();
                Thread.sleep(GOSSIP);
            }
        }catch (InterruptedException e){
            if(!shutdown){
                logger.severe("Gossiper thread interrupted");
            }
        }
    }

    private void processIncomingMessages() {
        Message message;
        while((message = incomingMessages.poll()) != null){
            if(message.getMessageType() != Message.MessageType.GOSSIP){
                incomingMessages.offer(message);
                continue;
            }
            verboseLogger.info(String.format("Received gossip from %s:%d - Content: %s, Time: %d",
                    message.getSenderHost(),
                    message.getSenderPort(),
                    new String(message.getMessageContents()),
                    System.currentTimeMillis()));

            updateGossipDataFromMessage(message.getMessageContents(), gossipData);
        }
    }

    private void sendGossip() {
        gossipData.updateHeartbeat(myPeerServer.getServerId());

        List<Long> peers = new ArrayList<>(peerIDtoAddress.keySet());
        peers.remove(myPeerServer.getServerId());

        int numOfPeersToGossip = (int) Math.sqrt(peers.size()) + 1;
        while(numOfPeersToGossip > 0 && !peers.isEmpty()){
            int rand = (int) (Math.random() * peers.size());
            long peerId = peers.remove(rand);

            if(!gossipData.isNodeFailed(peerId)){
                InetSocketAddress peerAddress = peerIDtoAddress.get(peerId);
                byte[] gossipMessage = buildGossipMessage(gossipData);
                Message message = new Message(Message.MessageType.GOSSIP, gossipMessage, myPeerServer.getAddress().getHostString(), myPeerServer.getUdpPort(),
                        peerAddress.getHostString(), peerAddress.getPort());
                outgoingMessages.offer(message);
            }
            numOfPeersToGossip--;
        }
    }

    private void checkFailedNodes() {
        long currentTime = System.currentTimeMillis();
        for(Map.Entry<Long,InetSocketAddress> entry : peerIDtoAddress.entrySet()){
            Long peerId = entry.getKey();
            if(peerId.equals(myPeerServer.getServerId()) || gossipData.isNodeFailed(peerId)){
                continue;
            }

            Long lastHeartbeatTime = gossipData.getLastHeartbeatTime(peerId);
            if(lastHeartbeatTime == null || (currentTime - lastHeartbeatTime) > FAIL){
                handleNodeFailure(peerId);
            }
        }
    }

    private void handleNodeFailure(long nodeId) {
        gossipData.markNodeFailed(nodeId);
        String message = String.format("%d: no heartbeat from server %d - SERVER FAILED", myPeerServer.getServerId(), nodeId);
        logger.warning(message);
        System.out.println(message);
        myPeerServer.reportFailedPeer(nodeId);
    }

    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }



}
