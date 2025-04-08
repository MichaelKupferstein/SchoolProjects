package edu.yu.cs.com3810.skoop.chat.dao;

public interface ChatDao {
    /**
     * Save a chat message.
     *
     * @param message the message to save
     * @return true if saved successfully, false otherwise
     */
    boolean saveMessage(ChatMessage message);

    /**
     * Get recent messages for a ride.
     *
     * @param rideId ID of the ride
     * @param limit maximum number of messages
     * @return list of recent messages
     */
    java.util.List<ChatMessage> getRecentMessages(String rideId, int limit);

    /**
     * Get messages before a specific message.
     *
     * @param rideId ID of the ride
     * @param fromMessageId ID of the message to start from
     * @param limit maximum number of messages
     * @return list of messages
     */
    java.util.List<ChatMessage> getMessagesBefore(String rideId, String fromMessageId, int limit);

    /**
     * Represents a chat message in the system.
     */
    public static class ChatMessage {
        private String id;
        private String rideId;
        private String senderId;
        private String senderName;
        private String content;
        private java.time.LocalDateTime timestamp;

        // Getters/setters
    }
}
