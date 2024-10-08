package edu.yu.cs.com3800.stage2;

import edu.yu.cs.com3800.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class PeerServerImpl extends Thread implements PeerServer {
    private final InetSocketAddress myAddress;
    private final int myPort;
    private PeerServer.ServerState state;
    private volatile boolean shutdown;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Long id;
    private long peerEpoch;
    private volatile Vote currentLeader;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private UDPMessageSender senderWorker;
    private UDPMessageReceiver receiverWorker;

    public PeerServerImpl(int myPort, long peerEpoch, Long id, Map<Long,InetSocketAddress> peerIDtoAddress){
        //code here...
        this.myPort = myPort;
        this.myAddress = peerIDtoAddress.get(id);
    }

    @Override
    public void shutdown(){
        this.shutdown = true;
        this.senderWorker.shutdown();
        this.receiverWorker.shutdown();
    }

    @Override
    public void setCurrentLeader(Vote v) throws IOException {

    }

    @Override
    public Vote getCurrentLeader() {
        return null;
    }

    @Override
    public void sendMessage(Message.MessageType type, byte[] messageContents, InetSocketAddress target) throws IllegalArgumentException {

    }

    @Override
    public void sendBroadcast(Message.MessageType type, byte[] messageContents) {

    }

    @Override
    public ServerState getPeerState() {
        return null;
    }

    @Override
    public void setPeerState(ServerState newState) {

    }

    @Override
    public Long getServerId() {
        return Long.valueOf("0");
    }

    @Override
    public long getPeerEpoch() {
        return 0;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public int getUdpPort() {
        return 0;
    }

    @Override
    public InetSocketAddress getPeerByID(long peerId) {
        return null;
    }

    @Override
    public int getQuorumSize() {
        return 0;
    }

    @Override
    public void run(){
        //step 1: create and run thread that sends broadcast messages
        //step 2: create and run thread that listens for messages sent to this server
        //step 3: main server loop
        try{
            while (!this.shutdown){
                switch (getPeerState()){
                    case LOOKING:
                        //start leader election, set leader to the election winner
                        break;
                }
            }
        }
        catch (Exception e) {
            //code...
        }
    }

}