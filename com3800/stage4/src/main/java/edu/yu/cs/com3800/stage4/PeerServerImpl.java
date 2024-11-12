package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.yu.cs.com3800.Message.MessageType.COMPLETED_WORK;
import static edu.yu.cs.com3800.Message.MessageType.WORK;


public class PeerServerImpl extends Thread implements PeerServer,LoggingServer {
    private final InetSocketAddress myAddress;
    private final int myPort;
    private ServerState state;
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
        setName("PeerServerImpl-port-" + this.myPort);
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
        this.logger.entering(PeerServerImpl.class.getName(),"sendMessage",new Object[]{type,messageContents,target});
        this.logger.fine("Sending message to " + target.getHostString() + ":" + target.getPort());

        Message msg;
        if(type == WORK || type == COMPLETED_WORK){
            try {
                msg = new Message(messageContents);
            }catch (IllegalArgumentException e) {
                msg = new Message(type,messageContents,this.myAddress.getHostString(),this.myPort,target.getHostString(),target.getPort());
            }
        }else{
            msg = new Message(type,messageContents,this.myAddress.getHostString(),this.myPort,target.getHostString(),target.getPort());

        }
        this.outgoingMessages.offer(msg);
    }

    @Override
    public void sendBroadcast(Message.MessageType type, byte[] messageContents) {
        this.logger.entering(PeerServerImpl.class.getName(),"sendBroadcast",new Object[]{type,messageContents});

        this.logger.fine("Sending broadcast message: " + type.name());
        for(InetSocketAddress peer : peerIDtoAddress.values()){
            if(!peer.equals(this.myAddress)){
                this.logger.fine("Sending message to " + peer.getHostString() + ":" + peer.getPort());
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
        this.logger.entering(PeerServerImpl.class.getName(),"run");
        try{
            this.senderWorker = new UDPMessageSender(this.outgoingMessages,this.myPort);
            this.senderWorker.start();
            this.logger.fine("Sender worker started on port " + this.myPort);
            this.receiverWorker = new UDPMessageReceiver(this.incomingMessages,this.myAddress,this.myPort,this);
            this.receiverWorker.start();
            this.logger.fine("Receiver worker started on port " + this.myPort);
        }catch(Exception e){
            this.logger.log(Level.WARNING,"Failed to start sender and receiver workers",e);
        }

        while (!this.shutdown){
            try {
                switch (getPeerState()){
                    case LOOKING:
                        this.logger.fine("Starting leader election");
                        LeaderElection election = new LeaderElection(this, this.incomingMessages, this.logger);
                        Vote leader = election.lookForLeader();
                        if (leader != null) {
                            setCurrentLeader(leader);
                            this.logger.fine("Leader elected: " + leader.getProposedLeaderID());

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
                        try {
                            Message message1 = this.incomingMessages.take();
                            if(message1 != null){
                                this.logger.fine("Following and received message: " + message1);
                                if(message1.getMessageType() == Message.MessageType.WORK){
                                    this.logger.fine("Follower received work");
                                    this.follower.work(message1);
                                }
                            }
                        } catch (Exception e) {
                            this.logger.log(Level.WARNING,"Error processing message in FOLLOWING state",e);
                            continue;
                        }
                        break;

                    case LEADING:
                        try {
                            Message message2 = this.incomingMessages.take();
                            if(message2 != null){
                                this.logger.fine("Leading and received message: " + message2);
                                if(message2.getMessageType() == Message.MessageType.COMPLETED_WORK){
                                    this.logger.fine("Leader received completed work");
                                    this.leader.processMessage(message2);
                                }else if(message2.getMessageType() == Message.MessageType.WORK){
                                    this.logger.fine("Leader received work");
                                    this.leader.processMessage(message2);
                                }
                            }
                        } catch (Exception e) {
                            this.logger.log(Level.WARNING,"Error processing message in LEADING state",e);
                            continue;
                        }
                        break;
                }
            } catch (Exception e) {
                this.logger.warning("Error in main server loop: " + e.getMessage());
                if (this.shutdown) {
                    this.logger.severe("PeerServerImpl failed while shutting down: " + e.getMessage());
                    break;
                }
            }
        }

        this.logger.info("PeerServerImpl shutting down normally");
    }

    private void startFollowing() {
        this.logger.entering(PeerServerImpl.class.getName(),"startFollowing");
        if(this.follower == null){
            try {
                this.logger.fine("Starting JavaRunnerFollower");
                this.follower = new JavaRunnerFollower(this);
                this.follower.start();
            } catch (IOException e) {
                logger.severe("Failed to start JavaRunnerFollower" + e.getMessage());
            }
        }
        if(leader != null){
            this.logger.fine("Shutting down RoundRobinLeader to start following");
            leader.shutdown();
            leader = null;
        }
    }

    private void startLeading() {
        this.logger.entering(PeerServerImpl.class.getName(),"startLeading");
        if(this.leader == null){
            this.logger.fine("Starting RoundRobinLeader");
            try {
                this.leader = new RoundRobinLeader(this,this.peerIDtoAddress);
            } catch (IOException e) {
                this.logger.severe("Failed to start RoundRobinLeader" + e.getMessage());
            }
            this.leader.start();
        }
        if(follower != null){
            this.logger.fine("Shutting down JavaRunnerFollower to start leading");
            follower.shutdown();
            follower = null;
        }
    }

    //for testing
    public boolean isInterrupted() {
        return this.shutdown;
    }

}