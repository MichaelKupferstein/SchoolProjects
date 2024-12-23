package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.Vote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

public class GatewayPeerServerImpl extends PeerServerImpl{

    private final Logger logger;

    public GatewayPeerServerImpl(int udpPort, long peerEpoch, Long id, Map<Long, InetSocketAddress> peerIDtoAddress, int numOfObservers) throws IOException {
        super(udpPort, peerEpoch, id, peerIDtoAddress,id, numOfObservers);
        setPeerState(ServerState.OBSERVER);
        this.logger = initializeLogging(GatewayPeerServerImpl.class.getCanonicalName() + "-on-port-" + udpPort);
        logger.info("GatewayPeerServer initialized on port " + udpPort);
    }

    @Override
    public void setCurrentLeader(Vote v) {
        super.setCurrentLeader(v);

        if(v != null && this.getPeerState() == ServerState.OBSERVER){
            startGossiper();
            logger.info("GatewayPeerServer detected new leader, starting gossiper");
        }
    }

    @Override
    public void setPeerState(ServerState state) {
        if(state != ServerState.OBSERVER){
            logger.warning("GatewayPeerServer cannot change state from OBSERVER to " + state);
            return;
        }
        super.setPeerState(state);
    }
}
