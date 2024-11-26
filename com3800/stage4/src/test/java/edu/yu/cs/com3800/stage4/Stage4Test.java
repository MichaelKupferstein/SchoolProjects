package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class Stage4Test {
    private final int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
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

        servers = new ArrayList<>(ports.length);
        for (int i = 0; i < ports.length; i++) {
            HashMap<Long, InetSocketAddress> map = new HashMap<>(peerIDtoAddress);
            PeerServerImpl server = new PeerServerImpl(ports[i], 0, (long) i,
                    map, (long) ports.length, 1);
            servers.add(server);
            server.start();
        }

        gateway = new GatewayServer(gatewayPort, observerPort, 0, (long) ports.length,
                new ConcurrentHashMap<>(peerIDtoAddress), 1);
        gateway.start();

        waitForElectionComplete();
        verifyClusterState();
    }

    private void waitForElectionComplete() throws InterruptedException {
        long start = System.currentTimeMillis();
        long timeout = TimeUnit.SECONDS.toMillis(15); // 15 second timeout
        boolean electionComplete = false;

        while (System.currentTimeMillis() - start < timeout) {
            electionComplete = true;
            for (PeerServerImpl server : servers) {
                if (server.getCurrentLeader() == null) {
                    electionComplete = false;
                    break;
                }
            }
            if (electionComplete) break;
            Thread.sleep(1000);
        }

        assertTrue(electionComplete, "Election failed to complete within timeout period");
    }

    private void verifyClusterState() {
        int leaderCount = 0;
        PeerServerImpl leader = null;
        for (PeerServerImpl server : servers) {
            if (server.getPeerState() == PeerServer.ServerState.LEADING) {
                leaderCount++;
                leader = server;
            }
        }
        assertEquals(1, leaderCount, "Should have exactly one leader");
        assertNotNull(leader, "Leader should not be null");

        for (PeerServerImpl server : servers) {
            if (server != leader) {
                assertEquals(PeerServer.ServerState.FOLLOWING, server.getPeerState(),
                        "Non-leader servers should be followers");
            }
        }
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

    @Test
    public void testBasicGatewayFunctionality() throws Exception {
        verifyClusterState();

        HttpURLConnection conn = null;
        try {
            conn = sendHttpRequest(validClass);
            assertEquals(200, conn.getResponseCode());
            String response = getResponse(conn);
            assertEquals("Hello, World!", response);
            assertEquals("false", conn.getHeaderField("Cached-Response"));
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
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