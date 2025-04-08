package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.Vote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

public class GatewayPeerServerImpl extends PeerServerImpl{

    private final Logger logger;
    private Gossiper gossiper;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    public GatewayPeerServerImpl(int udpPort, long peerEpoch, Long id, Map<Long, InetSocketAddress> peerIDtoAddress, int numOfObservers) throws IOException {
        super(udpPort, peerEpoch, id, peerIDtoAddress,id, numOfObservers);
        this.peerIDtoAddress = peerIDtoAddress;
        setPeerState(ServerState.OBSERVER);
        this.logger = initializeLogging(GatewayPeerServerImpl.class.getCanonicalName() + "-on-port-" + udpPort);
        logger.info("GatewayPeerServerImpl initialized on port " + udpPort);
    }


    @Override
    public void setCurrentLeader(Vote v) {
        super.setCurrentLeader(v);
        if(v== null){
            if(gossiper != null){
                gossiper.pause();
            }
            logger.info("GatewayPeerServerImpl detected leader failure,pause gossiper");
        }else {
            if(gossiper != null){
                gossiper.unpause();
            }
            logger.info("GatewayPeerServerImpl detected new leader,unpause gossiper");
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
