package edu.yu.cs.com3800.stage4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class GatewayPeerServerImpl extends PeerServerImpl{

    public GatewayPeerServerImpl(int udpPort, long peerEpoch, Long id, Map<Long, InetSocketAddress> peerIDtoAddress, int numOfObservers) throws IOException {
        super(udpPort, peerEpoch, id, peerIDtoAddress,id, numOfObservers);
        setPeerState(ServerState.OBSERVER);
    }

    @Override
    public void setPeerState(ServerState state) {
        if(state != ServerState.OBSERVER){
            throw new IllegalArgumentException("GatewayPeerServerImpl can only be an observer");
        }
        super.setPeerState(state);
    }
}
