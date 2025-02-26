package edu.yu.cs.com3810.skoop.user.dao;

import edu.yu.cs.com3810.skoop.user.UserProfileService;

/**
 * Interface for user profile data access.
 */
public interface UserProfileDao {

    /**
     * Get user profile by user ID.
     *
     * @param userId the user's ID
     * @return the user profile if found, null otherwise
     */
    UserProfile getUserProfileById(String userId);

    /**
     * Save a user profile.
     *
     * @param profile the profile to save
     * @return true if saved successfully, false otherwise
     */
    boolean saveUserProfile(UserProfile profile);

    /**
     * Get user ride history.
     *
     * @param userId the user's ID
     * @return the user's ride history
     */
    java.util.List<RideHistory> getUserRideHistory(String userId);

    /**
     * Represents a user profile in the system.
     */
    public static class UserProfile {
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;

        // Getters/setters would be here
    }

    /**
     * Represents an entry in a user's ride history.
     */
    public static class RideHistory {
        private String rideId;
        private String origin;
        private String destination;
        private java.time.LocalDateTime departureTime;
        private java.time.LocalDateTime arrivalTime;
        private double cost;

        // Getters/setters would be here
    }
}
