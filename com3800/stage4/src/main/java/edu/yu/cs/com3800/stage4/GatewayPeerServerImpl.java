package edu.yu.cs.com3800.stage4;

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
    public void setPeerState(ServerState state) {
        if(state != ServerState.OBSERVER){
            throw new IllegalArgumentException("GatewayPeerServerImpl can only be an observer");
        }
        super.setPeerState(state);
    }
}
