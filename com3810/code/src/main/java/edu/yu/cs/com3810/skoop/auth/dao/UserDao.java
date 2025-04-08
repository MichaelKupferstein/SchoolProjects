package edu.yu.cs.com3810.skoop.auth.dao;

/**
 * Interface for user data access.
 */
public interface UserDao {

    /**
     * Get user by email.
     *
     * @param email the user's email
     * @return the user if found, null otherwise
     */
    User getUserByEmail(String email);

    /**
     * Save a new user.
     *
     * @param user the user to save
     * @return true if saved successfully, false otherwise
     */
    boolean saveUser(User user);

    //other methods as needed

    /**
     * Represents a user in the system.
     */
    public static class User {
        private String id;
        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;

        // Getters/setters
    }

}
