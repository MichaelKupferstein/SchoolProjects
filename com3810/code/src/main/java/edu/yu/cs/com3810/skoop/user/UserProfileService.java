package edu.yu.cs.com3810.skoop.user;

import edu.yu.cs.com3810.skoop.user.dao.UserProfileDao;

/**
 * User profile service that manages user information and settings.
 */
public class UserProfileService {


    /**
     * Initialize the user profile service.
     *
     * @param userProfileDao data access object for user profiles
     */
    public void initialize(UserProfileDao userProfileDao) {
        // Set up the user profile service
        // Connect to the database via the DAO
    }

    /**
     * Get a user's profile.
     *
     * @param userId the ID of the user
     * @return the user's profile if found, null otherwise
     */
    public UserProfileDao.UserProfile getUserProfile(String userId) {
        // Retrieve the user's profile from the database
        return null;
    }

    /**
     * Update a user's profile.
     *
     * @param userId the ID of the user
     * @param firstName updated first name (null to keep current)
     * @param lastName updated last name (null to keep current)
     * @param phoneNumber updated phone number (null to keep current)
     * @return true if profile updated successfully, false otherwise
     */
    public boolean updateUserProfile(String userId, String firstName, String lastName, String phoneNumber) {
        // Update the user's profile in the database
        return false;
    }

    /**
     * Get a user's ride history.
     *
     * @param userId the ID of the user
     * @return the user's ride history
     */
    public java.util.List<UserProfileDao.RideHistory> getUserRideHistory(String userId) {
        // Retrieve the user's ride history from the database
        return null;
    }
}