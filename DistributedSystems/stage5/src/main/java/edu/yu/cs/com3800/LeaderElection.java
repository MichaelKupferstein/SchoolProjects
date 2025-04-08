package edu.yu.cs.com3800;

import edu.yu.cs.com3800.stage5.PeerServerImpl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.yu.cs.com3800.PeerServer.ServerState.OBSERVER;

/**We are implemeting a simplfied version of the election algorithm. For the complete version which covers all possible scenarios, see https://github.com/apache/zookeeper/blob/90f8d835e065ea12dddd8ed9ca20872a4412c78a/zookeeper-server/src/main/java/org/apache/zookeeper/server/quorum/FastLeaderElection.java#L913
 */
public class LeaderElection {

    private static Logger logger;

    /**
     * time to wait once we believe we've reached the end of leader election.
     */
    private final static int finalizeWait = 1600;

    /**
     * Upper bound on the amount of time between two consecutive notification checks.
     * This impacts the amount of time to get the system up again after long partitions. Currently 30 seconds.
     */
    private final static int maxNotificationInterval = 30000;
    private final static int initialNotifcationInterval = 200;
    private long proposedEpoch;
    private long proposedLeader;
    private PeerServer server;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Map<Long, ElectionNotification> votes;

    public LeaderElection(PeerServer server, LinkedBlockingQueue<Message> incomingMessages, Logger logger) {
        this.server = server;
        this.incomingMessages = incomingMessages;
        this.logger = logger;
        this.proposedEpoch = server.getPeerEpoch();
        this.proposedLeader = server.getServerId();
        this.logger = logger;
        this.votes = new HashMap<>();

        //System.out.println("LeaderElection created from server " + server.getServerId());
    }

    public static byte[] buildMsgContent(ElectionNotification notification) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 3 + Character.BYTES);
        buffer.putLong(notification.getProposedLeaderID());
        buffer.putChar(notification.getState().getChar());
        buffer.putLong(notification.getSenderID());
        buffer.putLong(notification.getPeerEpoch());
        return buffer.array();
    }

    public static ElectionNotification getNotificationFromMessage(Message received) {
        byte[] contents = received.getMessageContents();
        ByteBuffer buffer = ByteBuffer.wrap(contents);
        long proposedLeaderID = buffer.getLong();
        char stateChar = buffer.getChar();
        long senderID = buffer.getLong();
        long peerEpoch = buffer.getLong();
        return new ElectionNotification(proposedLeaderID, PeerServer.ServerState.getServerState(stateChar), senderID, peerEpoch);
    }

    /**
     * Note that the logic in the comments below does NOT cover every last "technical" detail you will need to address to implement the election algorithm.
     * How you store all the relevant state, etc., are details you will need to work out.
     * @return the elected leader
     */
    public synchronized Vote lookForLeader() {
        //System.out.println("Looking for leader");

        try {
            sendNotifications();
            int currentInterval = initialNotifcationInterval;

            while (!Thread.currentThread().isInterrupted()) {
                try{
                    Message message = this.incomingMessages.poll(currentInterval, TimeUnit.MILLISECONDS);

                    if (message == null) {
                        sendNotifications();
                        currentInterval = Math.min(currentInterval * 2, maxNotificationInterval);
                    } else {
                        currentInterval = initialNotifcationInterval;

                        ElectionNotification notification = getNotificationFromMessage(message);

                        if (notification.getPeerEpoch() < this.proposedEpoch) continue;

                        if(supersedesCurrentVote(notification.getProposedLeaderID(), notification.getPeerEpoch())){
                            this.proposedLeader = notification.getProposedLeaderID();
                            this.proposedEpoch = notification.getPeerEpoch();
                            sendNotifications();
                        }

                        this.votes.put(notification.getSenderID(), notification);

                        if (haveEnoughVotes(this.votes, new Vote(this.proposedLeader, this.proposedEpoch))) {
                            boolean gotHigherVote = false;

                            while(!gotHigherVote){
                                Message m = this.incomingMessages.poll(finalizeWait, TimeUnit.MILLISECONDS);
                                if(m !=null){
                                    ElectionNotification n = getNotificationFromMessage(m);
                                    if(supersedesCurrentVote(n.getProposedLeaderID(), n.getPeerEpoch())){
                                        this.proposedLeader = n.getProposedLeaderID();
                                        this.proposedEpoch = n.getPeerEpoch();
                                        this.votes.put(n.getSenderID(), n);
                                        gotHigherVote = true;
                                        break;
                                    }
                                    this.votes.put(n.getSenderID(), n);
                                } else{
                                    break;
                                }
                            }
                            if(!gotHigherVote){
                                ElectionNotification n1 = new ElectionNotification(this.proposedLeader, this.server.getPeerState(), this.server.getServerId(), this.server.getPeerEpoch());
                                return acceptElectionWinner(n1);
                            }

                        }
                    }
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    return null;
                }

            }
        }catch (Exception e){
            this.logger.log(Level.SEVERE,"Exception occurred during election; election canceled",e);
        }
        return null;
    }

    private void sendNotifications() {
        ElectionNotification n = new ElectionNotification(this.proposedLeader, this.server.getPeerState(), this.server.getServerId(), this.server.getPeerEpoch());
        byte[] content = buildMsgContent(n);
        this.server.sendBroadcast(Message.MessageType.ELECTION, content);
    }

    private Vote acceptElectionWinner(ElectionNotification n) {
        //set my state to either LEADING or FOLLOWING
        if(n.getProposedLeaderID() == this.server.getServerId()){
            this.server.setPeerState(PeerServer.ServerState.LEADING);
        }else{
            this.server.setPeerState(PeerServer.ServerState.FOLLOWING);
        }
        //clear out the incoming queue before returning
        this.incomingMessages.clear();
        return new Vote(n.getProposedLeaderID(), n.getPeerEpoch());

    }

    /*
     * We return true if one of the following three cases hold:
     * 1- New epoch is higher
     * 2- New epoch is the same as current epoch, but server id is higher.
     */
    protected boolean supersedesCurrentVote(long newId, long newEpoch) {
        if (this.server.getPeerState() == OBSERVER) {
            return false; // Observers shouldn't influence election
        }
        //if the new id is greater than the total number of servers, it is invalid
        //cast this.server to a PeerServerImpl to access the getNumServers method
        if(!((PeerServerImpl)this.server).containsId(newId)){
            return false;
        }

        // First compare epochs
        if (newEpoch > this.proposedEpoch) {
            return true;
        }
        if (newEpoch < this.proposedEpoch) {
            return false;
        }
        // If epochs are equal, compare IDs
        return newId > this.proposedLeader;
    }

    /**
     * Termination predicate. Given a set of votes, determines if we have sufficient support for the proposal to declare the end of the election round.
     * Who voted for who isn't relevant, we only care that each server has one current vote.
     */
    protected boolean haveEnoughVotes(Map<Long, ElectionNotification> votes, Vote proposal) {
        if (this.server.getPeerState() == OBSERVER) {
            return false;
        }

        int count = 0;

        for (ElectionNotification n : votes.values()) {
            if (n.getState() != OBSERVER) {
                if (n.getProposedLeaderID() == proposal.getProposedLeaderID() && n.getPeerEpoch() == proposal.getPeerEpoch()) {
                    count++;
                }
            }
        }

        return count >= this.server.getQuorumSize();
    }
}