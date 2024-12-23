package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.PeerServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;


public class Gossiper extends Thread implements LoggingServer {

    private static final int GOSSIP = 3000;
    private static final int FAIL = GOSSIP * 10;
    private static final int CLEANUP = FAIL *2;

    private PeerServer myPeerServer;
    private Map<Long, InetSocketAddress> peerIDtoAddress;
    private GossipData gossipData;
    private volatile boolean shutdown;
    private Logger summaryLogger;
    private Logger verboseLogger;
    private Random random;

    public Gossiper(PeerServer myPeerServer, Map<Long, InetSocketAddress> peerIDtoAddress) throws IOException {
        this.myPeerServer = myPeerServer;
        this.peerIDtoAddress = peerIDtoAddress;
        this.gossipData = new GossipData(peerIDtoAddress, myPeerServer.getServerId());
        this.summaryLogger = initializeLogging(Gossiper.class.getCanonicalName() + "-summary-on-port-" + myPeerServer.getUdpPort());
        this.verboseLogger = initializeLogging(Gossiper.class.getCanonicalName() + "-verbose-on-port-" + myPeerServer.getUdpPort(),true);
        this.random = new Random();
        setDaemon(true);
        setName("Gossiper-on-port-" + myPeerServer.getUdpPort());
    }

    @Override
    public void run(){
        while(!shutdown){
            try{
                gossipData.updateHeartbeat(myPeerServer.getServerId());
                List<InetSocketAddress> peers = selectRandomPeers();

                for(InetSocketAddress peer : peers){
                    sendGossipMessage(peer);
                }
                checkFailedNodes();

                Thread.sleep(GOSSIP);
            }catch(InterruptedException e){
                if(!shutdown){
                    summaryLogger.severe("Gossiper thread interrupted: " + e.getMessage());
                }
            }
        }
    }

    private List<InetSocketAddress> selectRandomPeers(){
        List<InetSocketAddress> allPeers = new ArrayList<>(peerIDtoAddress.values());
        allPeers.remove(myPeerServer.getAddress());
        List<InetSocketAddress> selectedPeers = new ArrayList<>();

        int numPeers = (int) Math.sqrt(allPeers.size()) + 1;
        for(int i = 0; i < numPeers; i++){
            int index = random.nextInt(allPeers.size());
            selectedPeers.add(allPeers.get(index));
            allPeers.remove(index);
        }
        return selectedPeers;
    }

    private void sendGossipMessage(InetSocketAddress peer){
        try{
            byte[] data = createGossipMessageData();

            Message gossipMessage = new Message(Message.MessageType.GOSSIP, data, myPeerServer.getAddress().getHostString(), myPeerServer.getUdpPort(),
                    peer.getHostString(),peer.getPort());

            verboseLogger.fine("Sending gossip message to " + peer.getPort() + ": " + formatGossipMessageContent(data));
            myPeerServer.sendMessage(Message.MessageType.GOSSIP,data,peer);
        }catch(Exception e){
            summaryLogger.severe("Error sending gossip message to " + peer + ": " + e.getMessage());
        }
    }

    private byte[] createGossipMessageData() {
        int numEntries = gossipData.getHeartbeats().size();
        int bufferSize = Integer.BYTES + (Long.BYTES * 3 * numEntries);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        Map<Long,Long> heartbeats = gossipData.getHeartbeats();

        buffer.putInt(heartbeats.size());
        long currentTime = System.currentTimeMillis();

        for(Map.Entry<Long,Long> entry : heartbeats.entrySet()) {
            buffer.putLong(entry.getKey());
            buffer.putLong(entry.getValue());
            buffer.putLong(currentTime);
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    public void handleGossipMessage(Message message){
        logGossipMessage(message);

        try{
            ByteBuffer buffer = ByteBuffer.wrap(message.getMessageContents());
            int numEntries = buffer.getInt();
            for(int i = 0; i < numEntries; i++){
                long nodeId = buffer.getLong();
                long heartbeat = buffer.getLong();
                long timestamp = buffer.getLong();

                Long currentHeartbeat = gossipData.getHeartbeat(nodeId);
                if(currentHeartbeat == null || heartbeat > gossipData.getHeartbeat(nodeId)){
                    gossipData.updateFromGossip(nodeId,heartbeat,timestamp);
                    logHeartbeatUpdate(nodeId,heartbeat,message.getSenderPort(),timestamp);
                }
            }

            verboseLogger.fine("Processed gossip message from " + message.getSenderPort() + ": " + formatGossipMessageContent(message.getMessageContents()));
        }catch(Exception e){
            summaryLogger.severe("Error handling gossip message: " + e.getMessage());
        }
    }

    private void checkFailedNodes(){
        long curretnTime = System.currentTimeMillis();

        for(Long nodeId : peerIDtoAddress.keySet()){
            if(nodeId.equals(myPeerServer.getServerId())){
                continue;
            }

            Long lastHeartbeatTime = gossipData.getLastHeartbeatTime(nodeId);
            if(lastHeartbeatTime == null){
                continue;
            }

            if(!gossipData.isNodeFailed(nodeId) && (curretnTime - lastHeartbeatTime) > FAIL){
                gossipData.markNodeFailed(nodeId);
                logNodeFailure(nodeId);
                myPeerServer.reportFailedPeer(nodeId);
            }
        }
    }

    public boolean isNodeFailed(long nodeId){
        return gossipData.isNodeFailed(nodeId);
    }

    private void logNodeFailure(long failedNodeId) {
        String message = String.format("%d: no heartbeat from server %d - SERVER FAILED", myPeerServer.getServerId(), failedNodeId);
        System.out.println(message);
        summaryLogger.warning(message);
    }

    private void logHeartbeatUpdate(long updatedNodeId, long newSequence, long sourceNodeId, long timestamp) {
        String message = String.format("%d: updated %d's heartbeat sequence to %d based on message from %d at node time %d",
                myPeerServer.getServerId(), updatedNodeId, newSequence, sourceNodeId, timestamp);
        summaryLogger.fine(message);
    }

    public void logStateChange(PeerServer.ServerState oldState, PeerServer.ServerState newState){
        String message = String.format("%d: switching from %s to %s", myPeerServer.getServerId(),oldState,newState);
        System.out.println(message);
        summaryLogger.warning(message);
    }

    private void logGossipMessage(Message message) {
        String logEntry = String.format("Received gossip message from %s:%d at time %d\nMessage contents:%s", message.getSenderHost(), message.getSenderPort(),
                System.currentTimeMillis(), formatGossipMessageContent(message.getMessageContents()));
        verboseLogger.fine(logEntry);
    }

    private String formatGossipMessageContent(byte[] contents) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(contents);
            int numEntries = buffer.getInt();
            StringBuilder sb = new StringBuilder();
            sb.append("\nNumber of entries: ").append(numEntries);

            for (int i = 0; i < numEntries && buffer.remaining() >= Long.BYTES * 3; i++) {
                long nodeId = buffer.getLong();
                long heartbeat = buffer.getLong();
                long timestamp = buffer.getLong();
                sb.append("\nEntry ").append(i)
                        .append(": NodeID=").append(nodeId)
                        .append(", Heartbeat=").append(heartbeat)
                        .append(", Timestamp=").append(timestamp);
            }
            return sb.toString();
        } catch (Exception e) {
            return "Failed to format message content: " + e.getMessage();
        }
    }

    public void shutdown(){
        this.shutdown = true;
        interrupt();
    }

}