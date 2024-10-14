package edu.yu.cs.com3800.stage2.tests;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.stage2.PeerServerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PeerServerImplTest {

    private ArrayList<PeerServer> servers;
    private HashMap<Long, InetSocketAddress> peerIDtoAddress;

    @BeforeEach
    public void setUp() throws IOException {
        peerIDtoAddress = new HashMap<>();
        peerIDtoAddress.put(1L, new InetSocketAddress("localhost", 8010));
        peerIDtoAddress.put(2L, new InetSocketAddress("localhost", 8020));
        peerIDtoAddress.put(3L, new InetSocketAddress("localhost", 8030));
        peerIDtoAddress.put(4L, new InetSocketAddress("localhost", 8040));
        peerIDtoAddress.put(5L, new InetSocketAddress("localhost", 8050));
        peerIDtoAddress.put(6L, new InetSocketAddress("localhost", 8060));
        peerIDtoAddress.put(7L, new InetSocketAddress("localhost", 8070));
        peerIDtoAddress.put(8L, new InetSocketAddress("localhost", 8080));

        servers = new ArrayList<>(peerIDtoAddress.size());
        for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
            HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
            map.remove(entry.getKey());
            PeerServerImpl server = new PeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map);
            servers.add(server);
            new Thread(server, "Server on port " + server.getAddress().getPort()).start();
        }
    }

    @AfterEach
    public void tearDown(){
        for(PeerServer server : servers){
            server.shutdown();
        }
    }

    @Test
    public void testLeaderElection() throws InterruptedException {
        int maxDos = 15;
        int retryInterval = 1000; // 1 second
        boolean allServersHaveLeader = false;

        for (int i = 0; i < maxDos; i++) {
            allServersHaveLeader = true;
            for (PeerServer server : servers) {
                if (server.getCurrentLeader() == null) {
                    allServersHaveLeader = false;
                    break;
                }
            }
            if (allServersHaveLeader) {
                break;
            }
            Thread.sleep(retryInterval);
        }

        assert allServersHaveLeader : "Not all servers have a leader after waiting";



        for (PeerServer server : servers) {
            assertNotNull(server.getCurrentLeader());
            assertEquals(8, server.getCurrentLeader().getProposedLeaderID());
        }
    }
}
