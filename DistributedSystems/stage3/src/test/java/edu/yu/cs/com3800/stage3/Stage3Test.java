package edu.yu.cs.com3800.stage3;

import edu.yu.cs.com3800.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

public class Stage3Test {

    private String validClass = "package edu.yu.cs.fall2019.com3800.stage1;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    //private int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
    private int[] ports = {8010, 8020};
    private int leaderPort = this.ports[this.ports.length - 1];
    private int myPort = 9999;
    private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
    private ArrayList<PeerServer> servers;
    private int requestCounter = 0;
    private UDPMessageSender sender;
    private UDPMessageReceiver receiver;

    @BeforeEach
    public void setUp() throws Exception {
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.servers = new ArrayList<>(ports.length);
        createServers();
        Thread.sleep(5000);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        if (sender != null) sender.shutdown();
        if (receiver != null) receiver.shutdown();
        stopServers();
        outgoingMessages.clear();
        incomingMessages.clear();
        servers.clear();
        Thread.sleep(3000);

        sender = null;
        receiver = null;
    }

    private void createServers() throws IOException {
        HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>();
        for(int i = 0; i < this.ports.length; i++) {
            InetSocketAddress address = new InetSocketAddress("localhost", this.ports[i]);
            peerIDtoAddress.put((long) i, address);
        }
        for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
            HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
            map.remove(entry.getKey());
            PeerServerImpl server = new PeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map);
            this.servers.add(server);
            server.start();
        }
    }

    private void stopServers() {
        for (PeerServer server : this.servers) {
            server.shutdown();
        }
    }

    private void initializeCommunication() throws IOException {
        if (sender != null) sender.shutdown();
        if (receiver != null) receiver.shutdown();
        sender = new UDPMessageSender(this.outgoingMessages, this.myPort);
        receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort, null);
        Util.startAsDaemon(sender, "Sender thread");
        Util.startAsDaemon(receiver, "Receiver thread");
    }

    @Test
    public void testServerShutdown() {
        stopServers();
        for (PeerServer server : this.servers) {
            assertTrue(((PeerServerImpl) server).isInterrupted(), "Server should be interrupted after shutdown");
        }
    }

    @Test
    public void testLeaderElection() {
        printLeaders();
        for (PeerServer server : this.servers) {
            Vote leader = server.getCurrentLeader();
            assertNotNull(leader, "Leader should not be null");
            if (server.getPeerState() != PeerServer.ServerState.LEADING) {
                assertEquals(leaderPort, server.getPeerByID(leader.getProposedLeaderID()).getPort(), "Leader should be on the expected port");
            }
        }
    }

    @Test
    public void testMessageProcessing() throws Exception {
        initializeCommunication();

        for (int i = 0; i < this.ports.length; i++) {
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendMessage(code);
        }

        String completeResponse = "";
        for (int i = 0; i < this.ports.length; i++) {
            Message msg = this.incomingMessages.take();
            String response = new String(msg.getMessageContents());
            completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
        }
        System.out.println(completeResponse);
        assertTrue(completeResponse.contains("Hello world! from code version"), "Responses should contain the modified message");
    }

    @Test
    public void testHandlingInvalidCode() throws Exception {
        initializeCommunication();

        String invalidCode = "public class Invalid { public void run() { return 1; } }";
        sendMessage(invalidCode);

        Message msg = this.incomingMessages.take();
        String response = new String(msg.getMessageContents());
        assertTrue(response.contains("Code did not compile"), "System should handle invalid code correctly");
    }

    private void printLeaders() {
        for (PeerServer server : this.servers) {
            Vote leader = server.getCurrentLeader();
            if (leader != null) {
                System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
            }
        }
    }

    private void sendMessage(String code) throws InterruptedException {
        Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort, requestCounter++);
        this.outgoingMessages.put(msg);
    }
}