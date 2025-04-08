package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;
import edu.yu.cs.com3800.stage5.PeerServerImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


import static org.junit.jupiter.api.Assertions.*;

public class Stage5Test {
    //private final int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
    private final int[] ports = {8010, 8020, 8030, 8040, 8050};
    private GatewayServer gateway;
    private ArrayList<PeerServerImpl> servers;
    private final String validClass = "public class Test { public String run() { return \"Hello, World!\"; } }";
    private final int gatewayPort = 8888;
    private final int observerPort = 9000;

    @BeforeEach
    public void setUp() throws Exception {
        HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>();
        for (int i = 0; i < ports.length; i++) {
            peerIDtoAddress.put((long) i, new InetSocketAddress("localhost", ports[i]));
        }
        peerIDtoAddress.put((long) ports.length, new InetSocketAddress("localhost", observerPort));

        createServers(peerIDtoAddress);

        Thread.sleep(5000);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (gateway != null) {
            gateway.shutdown();
        }
        if (servers != null) {
            for (PeerServerImpl server : servers) {
                server.shutdown();
            }
        }
        Thread.sleep(3000);
    }

    private void createServers(HashMap<Long, InetSocketAddress> peerIDtoAddress) throws IOException {
        servers = new ArrayList<>(ports.length);

        for (int i = 0; i < ports.length; i++) {
            HashMap<Long, InetSocketAddress> map = new HashMap<>(peerIDtoAddress);
            PeerServerImpl server = new PeerServerImpl(ports[i], 0, (long) i,
                    map, (long) ports.length, 1);
            servers.add(server);
            server.start();
            //System.out.println("Server " + server.getServerId() + " started");
        }

        gateway = new GatewayServer(gatewayPort, observerPort, 0, (long) ports.length,
                new ConcurrentHashMap<>(peerIDtoAddress), 1);
        gateway.start();
        //System.out.println("Gateway GatewayPeerServerImpl started with id " + gateway.getPeerServer().getServerId());
    }

    @Test
    public void testGossipProtocol() throws Exception {
        Thread.sleep(10000);

        for (PeerServerImpl server : servers) {
            assertFalse(server.isPeerDead(gateway.getPeerServer().getAddress()));
            for (PeerServerImpl other : servers) {
                if (other != server) {
                    assertFalse(server.isPeerDead(other.getAddress()));
                    //System.out.println("Server " + server.getServerId() + " is aware of server " + other.getServerId());
                }
            }
        }
    }

    @Test
    public void testNodeFailureDetection() throws Exception {
//        for(PeerServerImpl server : servers){
//            //print out how this server views all other servers
//            System.out.println("Server " + server.getServerId() +"(" + server.getPeerState() + ")"+   " views:");
//            for(PeerServerImpl other : servers){
//                System.out.println("\tServer " + other.getServerId() + " as " + (server.isPeerDead(other.getServerId()) ? "dead" : "alive"));
//            }
//        }

        Thread.sleep(10000);

        PeerServerImpl followerToKill = null;
        for (PeerServerImpl server : servers) {
            if (server.getPeerState() == PeerServer.ServerState.FOLLOWING) {
                followerToKill = server;
                break;
            }
        }
        assertNotNull(followerToKill);

        long killedServerId = followerToKill.getServerId();
        System.out.println("Shutting down server " + killedServerId);

        followerToKill.shutdown();

        Thread.sleep(45000);

        boolean failureDetected = false;
        for (PeerServerImpl server : servers) {
            if (server != followerToKill) {
                if (server.isPeerDead(killedServerId)) {
                    failureDetected = true;
                }else{
                    System.out.println("Server " + server.getServerId() + " is not aware of failure of server " + killedServerId);
                    failureDetected = false;
                }
            }
        }

//        for(PeerServerImpl server : servers){
//            System.out.println("Server " + server.getServerId() +"(" + server.getPeerState() + ")"+   " views:");
//            for(PeerServerImpl other : servers){
//                System.out.println("\tServer " + other.getServerId() + " as " + (server.isPeerDead(other.getServerId()) ? "dead" : "alive"));
//            }
//        }
        assertTrue(failureDetected, "No servers detected the failure of server " + killedServerId);
    }


    @Test
    public void testWorkReassignmentAfterFailure() throws Exception {
        Thread.sleep(5000);
        for (int i = 0; i < 5; i++) {
            HttpURLConnection conn = sendHttpRequest(validClass);
            assertEquals(200, conn.getResponseCode());
            conn.disconnect();
        }

        PeerServerImpl workerToKill = null;
        for (PeerServerImpl server : servers) {
            if (server.getPeerState() == PeerServer.ServerState.FOLLOWING) {
                workerToKill = server;
                break;
            }
        }
        assertNotNull(workerToKill);
        workerToKill.shutdown();

        for (int i = 0; i < 5; i++) {
            HttpURLConnection conn = sendHttpRequest(validClass);
            assertEquals(200, conn.getResponseCode());
            assertEquals("Hello, World!", getResponse(conn));
            conn.disconnect();
        }
    }

    @Test
    public void testLeaderFailure() throws Exception {
        Thread.sleep(5000);

        PeerServerImpl initialLeader = null;
        for (PeerServerImpl server : servers) {
            if (server.getPeerState() == PeerServer.ServerState.LEADING) {
                initialLeader = server;
                break;
            }
        }
        assertNotNull(initialLeader, "No leader was elected");
        long initialLeaderId = initialLeader.getServerId();
        System.out.println("Initial leader is server with ID: " + initialLeaderId);

        for (int i = 0; i < 3; i++) {
            HttpURLConnection conn = sendHttpRequest(validClass);
            assertEquals(200, conn.getResponseCode());
            assertEquals("Hello, World!", getResponse(conn));
            conn.disconnect();
        }

        System.out.println("Killing leader with ID: " + initialLeaderId);
        initialLeader.shutdown();

        Thread.sleep(30000);

        System.out.println("\nServer states after leader shutdown:");
        for (PeerServerImpl server : servers) {
            if (server != initialLeader) {
                System.out.println("Server " + server.getServerId() + " state: " + server.getPeerState() +
                        " (isDead " + initialLeaderId + ": " + server.isPeerDead(initialLeaderId) + ")");
            }
        }

        Thread.sleep(30000);

        PeerServerImpl newLeader = null;
        for (PeerServerImpl server : servers) {
            if (server != initialLeader && server.getPeerState() == PeerServer.ServerState.LEADING) {
                newLeader = server;
                break;
            }
        }
        System.out.println("\nFinal server states:");
        for (PeerServerImpl server : servers) {
            if (server != initialLeader) {
                System.out.println("Server " + server.getServerId() + " state: " + server.getPeerState());
            }
        }

        assertNotNull(newLeader, "No new leader was elected after leader failure");
        assertNotEquals(initialLeaderId, newLeader.getServerId(), "New leader should be different from failed leader");

        System.out.println("New leader is server with ID: " + newLeader.getServerId());
        Thread.sleep(5000);

        for (int i = 0; i < 3; i++) {
            HttpURLConnection conn = sendHttpRequest(validClass);
            assertEquals(200, conn.getResponseCode(), "Request failed after leader change");
            assertEquals("Hello, World!", getResponse(conn), "Unexpected response after leader change");
            conn.disconnect();
        }

        for (PeerServerImpl server : servers) {
            System.out.println("Server " + server.getServerId() + " state: " + server.getPeerState() + " leader: " + server.getCurrentLeader());
        }

    }


    private HttpURLConnection getLogConnection(String path) throws IOException {
        URL url = new URL("http://localhost:" + gatewayPort + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return conn;
    }

    private PeerServerImpl getCurrentLeader() {
        for (PeerServerImpl server : servers) {
            if (server.getPeerState() == PeerServer.ServerState.LEADING) {
                return server;
            }
        }
        return null;
    }

    private HttpURLConnection sendHttpRequest(String code) throws IOException {
        URL url = new URL("http://localhost:" + gatewayPort + "/compileandrun");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/x-java-source");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = code.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return conn;
    }

    private String getResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getResponseCode() == 200 ? conn.getInputStream() :
                conn.getErrorStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }


}