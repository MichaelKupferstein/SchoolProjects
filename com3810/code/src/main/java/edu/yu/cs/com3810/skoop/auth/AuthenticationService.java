package edu.yu.cs.com3810.skoop.auth;

import edu.yu.cs.com3810.skoop.auth.dao.UserDao;

/**
 * Authentication service that handles user authentication, verification, and session management.
 */
public class AuthenticationService {

    /**
     * Initialize the authentication service.
     *
     * @param userDao data access object for user information
     */
    public void initialize(UserDao userDao) {
        // Set up the authentication service
        // Connect to the database via the DAO
    }

    /**
     * Authenticate a user with email and password.
     *
     * @param email university email address
     * @param password user password
     * @return authentication token if successful, null otherwise
     */
    public String authenticate(String email, String password) {
        // Verify credentials against stored user information
        // Create and return a session token
        return null;
    }

    /**
     * Verify that an email is a valid university email.
     *
     * @param email the email to verify
     * @return true if the email is a valid university email, false otherwise
     */
    public boolean verifyUniversityEmail(String email) {
        // Check if the email domain belongs to a recognized university
        // This could involve a whitelist of university domains
        return false;
    }

    /**
     * Register a new user.
     *
     * @param email university email address
     * @param password user password
     * @param firstName user's first name
     * @param lastName user's last name
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(String email, String password, String firstName, String lastName) {
        // Verify the email is a valid university email
        // Hash the password
        // Store the user information
        return false;
    }

    /**
     * Validate a session token.
     *
     * @param token the session token to validate
     * @return the user ID if the token is valid, null otherwise
     */
    public String validateToken(String token) {
        // Check if the token exists and is not expired
        // Return the associated user ID
        return null;
    }

    /**
     * Log out a user by invalidating their session token.
     *
     * @param token the session token to invalidate
     */
    public void logout(String token) {
        // Invalidate the session token
    }



}
