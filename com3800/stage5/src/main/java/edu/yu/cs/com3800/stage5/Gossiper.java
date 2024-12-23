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
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Map<Long,Long> heartbeats = gossipData.getHeartbeats();

            buffer.putInt(heartbeats.size());
            for(Map.Entry<Long,Long> entry : heartbeats.entrySet()){
                buffer.putLong(entry.getKey());
                buffer.putLong(entry.getValue());
                buffer.putLong(System.currentTimeMillis());
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            Message gossipMessage = new Message(Message.MessageType.GOSSIP, data, myPeerServer.getAddress().getHostString(), myPeerServer.getUdpPort(),
                    peer.getHostString(),peer.getPort());
            myPeerServer.sendMessage(Message.MessageType.GOSSIP,data,peer);

            verboseLogger.fine("Sent gossip message to " + peer.getPort() + ": " + gossipMessage);
        }catch(Exception e){
            summaryLogger.severe("Error sending gossip message to " + peer + ": " + e.getMessage());
        }
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

                if(heartbeat > gossipData.getHeartbeat(nodeId)){
                    gossipData.updateFromGossip(nodeId,heartbeat,timestamp);
                    logHeartbeatUpdate(nodeId,heartbeat,message.getSenderPort(),timestamp);
                }
            }

            verboseLogger.fine("Processed gossip message from " + message.getSenderPort() + ": " + message);
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
        String logEntry = String.format("Received gossip message from %s:%d at time %d\nMessage contents:\n%s", message.getSenderHost(), message.getSenderPort(),
                System.currentTimeMillis(), message.toString());
        verboseLogger.fine(logEntry);
    }

    public void shutdown(){
        this.shutdown = true;
        interrupt();
    }

}