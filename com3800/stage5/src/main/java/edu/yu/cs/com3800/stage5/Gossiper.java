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
    private volatile boolean paused = true;

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

    public void pause(){
        this.paused = true;
        this.summaryLogger.fine("Gossiper paused");
    }

    public void unpause(){
        this.paused = false;
        this.summaryLogger.fine("Gossiper unpaused");
    }


    @Override
    public void run(){
        while(!shutdown){
            try{
                if(paused){
                    Thread.sleep(1000);
                    continue;
                }

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

        int numPeers = Math.max(2,(int) Math.sqrt(allPeers.size()) + 1);
        while (selectedPeers.size() < numPeers && !allPeers.isEmpty()) {
            int index = random.nextInt(allPeers.size());
            InetSocketAddress selected = allPeers.remove(index);
            selectedPeers.add(selected);
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
        ByteBuffer buffer = ByteBuffer.allocate(1024);
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
        if(paused){
            verboseLogger.fine("Gossiper is paused, ignoring gossip message from " + message.getSenderPort());
            return;
        }

        logGossipMessage(message);

        try{
            ByteBuffer buffer = ByteBuffer.wrap(message.getMessageContents());
            int numEntries = buffer.getInt();
            for(int i = 0; i < numEntries && buffer.remaining() >= 24; i++){
                long nodeId = buffer.getLong();
                long heartbeat = buffer.getLong();
                long timestamp = buffer.getLong();

                if(gossipData.isNodeFailed(nodeId)){
                    verboseLogger.fine("Ignoring gossip message from failed node " + nodeId);
                    continue;
                }

                Long currentHeartbeat = gossipData.getHeartbeat(nodeId);
                if(currentHeartbeat == null || heartbeat > gossipData.getHeartbeat(nodeId)){
                    gossipData.updateFromGossip(nodeId,heartbeat,timestamp);
                    logHeartbeatUpdate(nodeId,heartbeat,message.getSenderPort(),timestamp);
                }
            }

            verboseLogger.fine("Processed gossip message from " + message.getSenderPort());
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
            }else if(gossipData.isNodeFailed(nodeId) && (curretnTime - lastHeartbeatTime) > CLEANUP){
                gossipData.removeNode(nodeId);
                logNodeCleanup(nodeId);
            }
        }
    }

    private void logNodeCleanup(long nodeId) {
        String message = String.format("%d: cleaning up failed node %d", myPeerServer.getServerId(), nodeId);
        verboseLogger.warning(message);
    }

    public boolean isNodeFailed(long nodeId){
        return gossipData.isNodeFailed(nodeId);
    }

    private void logNodeFailure(long failedNodeId) {
        String message = String.format("%d: no heartbeat from server %d - SERVER FAILED", myPeerServer.getServerId(), failedNodeId);
        System.out.println(message);
        //System.out.println("Server with ID: " + myPeerServer.getServerId() + " is in state " + myPeerServer.getPeerState());
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