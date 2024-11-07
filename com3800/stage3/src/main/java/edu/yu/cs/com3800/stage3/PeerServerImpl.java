package edu.yu.cs.com3800.stage3;

import edu.yu.cs.com3800.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class PeerServerImpl extends Thread implements PeerServer,LoggingServer {
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
    private JavaRunnerFollower follower;
    private RoundRobinLeader leader;

    private static Logger logger;


    public PeerServerImpl(int myPort, long peerEpoch, Long id, Map<Long,InetSocketAddress> peerIDtoAddress) throws IOException {
        this.myAddress = new InetSocketAddress("localhost",myPort);
        this.myPort = myPort;
        this.state = ServerState.LOOKING;
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.id = id;
        this.peerEpoch = peerEpoch;
        this.peerIDtoAddress = peerIDtoAddress;
        this.logger = initializeLogging(PeerServerImpl.class.getCanonicalName() + "-on-port-" + this.myPort);
    }

    @Override
    public void shutdown(){
        this.shutdown = true;
        if(this.senderWorker != null)this.senderWorker.shutdown();
        if(this.receiverWorker != null)this.receiverWorker.shutdown();
        if(this.follower != null)this.follower.shutdown();
        if(this.leader != null)this.leader.shutdown();

    }

    @Override
    public void setCurrentLeader(Vote v){
        this.currentLeader = v;
    }

    @Override
    public Vote getCurrentLeader() {
        return this.currentLeader;
    }

    @Override
    public void sendMessage(Message.MessageType type, byte[] messageContents, InetSocketAddress target) throws IllegalArgumentException {
        Message msg = new Message(type,messageContents,this.myAddress.getHostString(),this.myPort,target.getHostString(),target.getPort());
        this.outgoingMessages.offer(msg);
    }

    @Override
    public void sendBroadcast(Message.MessageType type, byte[] messageContents) {
        for(InetSocketAddress peer : peerIDtoAddress.values()){
            if(!peer.equals(this.myAddress)){
                sendMessage(type,messageContents,peer);
            }
        }
    }

    @Override
    public ServerState getPeerState() {
        return this.state;
    }

    @Override
    public void setPeerState(ServerState newState) {
        this.state = newState;
    }

    @Override
    public Long getServerId() {
        return this.id;
    }

    @Override
    public long getPeerEpoch() {
        return this.peerEpoch;
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.myAddress;
    }

    @Override
    public int getUdpPort() {
        return this.myPort;
    }

    @Override
    public InetSocketAddress getPeerByID(long peerId) {
        return this.peerIDtoAddress.get(peerId);
    }

    @Override
    public int getQuorumSize() {
        return (this.peerIDtoAddress.size()/2)+1;
    }

    @Override
    public void run(){
        try{
            this.senderWorker = new UDPMessageSender(this.outgoingMessages,this.myPort);
            this.senderWorker.start();
            this.receiverWorker = new UDPMessageReceiver(this.incomingMessages,this.myAddress,this.myPort,this);
            this.receiverWorker.start();
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        try{
            while (!this.shutdown){
                switch (getPeerState()){
                    case LOOKING:
                        //start leader election, set leader to the election winner
                        LeaderElection election = new LeaderElection(this, this.incomingMessages, this.logger);
                        Vote leader = election.lookForLeader();
                        if (leader != null) {
                            setCurrentLeader(leader);

                            if(leader.getProposedLeaderID() == this.id) {
                                setPeerState(ServerState.LEADING);
                                startLeading();
                            }else {
                                setPeerState(ServerState.FOLLOWING);
                                startFollowing();
                            }
                        }
                        break;
                    case FOLLOWING:
                        Message message1 = this.incomingMessages.poll(3000, TimeUnit.MILLISECONDS);
                        if(message1 != null){
                            if(message1.getMessageType() == Message.MessageType.WORK){
                                this.follower.work(message1); //TODO: implement work method
                            }
                        }
                        break;
                    case LEADING:
                        Message message2 = this.incomingMessages.poll(3000, TimeUnit.MILLISECONDS);
                        if(message2 != null){
                            if(message2.getMessageType() == Message.MessageType.COMPLETED_WORK){
                                this.leader.processMessage(message2); //TODO: implement processMessage method
                            }
                        }
                        break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void startFollowing() {
        if(this.follower == null){
            try {
                this.follower = new JavaRunnerFollower(this,this.logger); //TODO: implement JavaRunnerFollower
                this.follower.start();
            } catch (IOException e) {
                logger.severe("Failed to start JavaRunnerFollower" + e.getMessage());
            }
        }
        if(leader != null){
            leader.shutdown(); //TODO: implement shutdown method
            leader = null;
        }
    }

    private void startLeading() {
        if(this.leader == null){
            this.leader = new RoundRobinLeader(this,this.peerIDtoAddress,this.logger); //TODO: implement RoundRobinLeader
            this.leader.start();
        }
        if(follower != null){
            follower.shutdown(); //TODO: implement shutdown method
            follower = null;
        }
    }

}