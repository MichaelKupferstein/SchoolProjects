package edu.yu.cs.com3800.stage4;

import edu.yu.cs.com3800.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.yu.cs.com3800.Message.MessageType.*;


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
    private Long gatewayID;
    private int numberOfObservers;

    private UDPMessageSender senderWorker;
    private UDPMessageReceiver receiverWorker;
    private JavaRunnerFollower follower;
    private RoundRobinLeader leader;

    private static Logger logger;


    public PeerServerImpl(int myPort, long peerEpoch, Long id, Map<Long,InetSocketAddress> peerIDtoAddress, Long gatewayID, int numberOfObservers) throws IOException {
        this.myAddress = new InetSocketAddress("localhost",myPort);
        this.myPort = myPort;
        this.state = ServerState.LOOKING;
        this.outgoingMessages = new LinkedBlockingQueue<>();
        this.incomingMessages = new LinkedBlockingQueue<>();
        this.id = id;
        this.peerEpoch = peerEpoch;
        this.peerIDtoAddress = peerIDtoAddress;
        this.gatewayID = gatewayID;
        this.numberOfObservers = numberOfObservers;
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
        interrupt();

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

        if(type == ELECTION && getServerId().equals(gatewayID)){
            this.logger.warning("GatewayPeerServerImpl cannot send election messages");
            return;
        }

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
        int voters = this.peerIDtoAddress.size() - this.numberOfObservers;
        return (voters/2)+1;
    }

    public Long getGatewayID() {
        return this.gatewayID;
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
            return;
        }

        while (!this.shutdown){
            try {
                switch (getPeerState()){
                    case LOOKING:
                        if(getServerId().equals(gatewayID)) {
                            setPeerState(ServerState.OBSERVER);
                            break;
                        }

                        this.logger.fine("Starting leader election");
                        LeaderElection election = new LeaderElection(this, this.incomingMessages, this.logger);
                        Vote leader = election.lookForLeader();
                        if (leader != null) {
                            setCurrentLeader(leader);
                            this.logger.fine("Leader elected: " + leader.getProposedLeaderID());

                            if(leader.getProposedLeaderID() == this.id) {
                                setPeerState(ServerState.LEADING);
                            }else{
                                setPeerState(ServerState.FOLLOWING);
                            }
                        }
                        break;

                    case FOLLOWING:
                        startFollowing();
                        break;

                    case LEADING:
                        startLeading();
                        break;

                    case OBSERVER:
                        try {
                            Message message = this.incomingMessages.poll(1000, TimeUnit.MILLISECONDS);
                            if (message != null && message.getMessageType() == Message.MessageType.ELECTION) {
                                ElectionNotification notification = LeaderElection.getNotificationFromMessage(message);
                                logger.info("Observer received election message from " + notification.getSenderID() +
                                        " in state " + notification.getState());
                                if (notification.getState() == ServerState.LEADING) {
                                    setCurrentLeader(new Vote(notification.getProposedLeaderID(), notification.getPeerEpoch()));
                                    logger.info("Observer recognized leader: " + getCurrentLeader().getProposedLeaderID());
                                }
                            }
                        } catch (InterruptedException e) {
                            if (shutdown) {
                                break;
                            }
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

        if(getServerId() == gatewayID){
            this.logger.warning("GatewayPeerServerImpl cannot lead");
            return;
        }

        this.logger.entering(PeerServerImpl.class.getName(),"startLeading");
        if(this.leader == null){
            this.logger.fine("Starting RoundRobinLeader");
            try {
                this.leader = new RoundRobinLeader(this,this.peerIDtoAddress,this.incomingMessages);
                ElectionNotification n = new ElectionNotification(this.id,ServerState.LEADING,this.id,this.peerEpoch);
                byte[] content = LeaderElection.buildMsgContent(n);
                this.sendBroadcast(ELECTION,content);
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

    public int getPeerIDtoAddressSize() {
        return this.peerIDtoAddress.size();
    }

    public int getNumberOfObservers() {
        return this.numberOfObservers;
    }

}