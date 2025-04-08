package edu.yu.cs.com3810.skoop.client;


import java.time.LocalDateTime;
import java.util.List;

/**
 * SimpleClient class that simulates a user-facing interface for the Skoop system.
 * This replaces the mobile app in the architecture but provides similar functionality.
 */
public class SimpleClient {


    /**
     * Initialize the client and connect to the Skoop API.
     *
     * @param serverUrl the URL of the Skoop API server
     */
    public void initialize(String serverUrl) {
        // Initialize connection to the Skoop API
        // Set up event listeners for real-time updates
    }

    /**
     * Login to the Skoop system using university credentials.
     *
     * @param email university email address
     * @param password user password
     * @return true if login was successful, false otherwise
     */
    public boolean login(String email, String password) {
        // Call authentication API to verify credentials
        // Store authentication token for subsequent requests
        return false;
    }

    /**
     * Register a new user in the Skoop system.
     *
     * @param email university email address
     * @param password user password
     * @param firstName user's first name
     * @param lastName user's last name
     * @return true if registration was successful, false otherwise
     */
    public boolean register(String email, String password, String firstName, String lastName) {
        // Call authentication API to register new user
        // Verify the email is a valid university email
        return false;
    }

    /**
     * Update user profile.
     *
     * @param firstName updated first name (null to keep current)
     * @param lastName updated last name (null to keep current)
     * @param phoneNumber updated phone number (null to keep current)
     * @return true if profile updated successfully, false otherwise
     */
    public boolean updateProfile(String firstName, String lastName, String phoneNumber) {
        // Call user profile API to update profile
        return false;
    }

    /**
     * Retrieves a list of all available rides.
     *
     * @return a list of RideSearchResult objects representing all rides
     */
    public List<RideSearchResult> getCurrentRides() {
        // Call ride management API to get all rides
        return null;
    }

    /**
     * Search for available rides based on various criteria.
     *
     * @param destination destination location
     * @param departureTime earliest departure time
     * @param maxCost maximum cost user is willing to pay
     * //@param TBD
     * @return list of matching rides
     */
    public List<RideSearchResult> filterRides(String destination, LocalDateTime departureTime, double maxCost) {
        // Call ride management API to search for rides
        return null;
    }

    /**
     * Get detailed information about a specific ride.
     *
     * @param rideId ID of the ride to get info about
     * @return RideSearchResult object with detailed ride information
     */
    public RideSearchResult getRideInfo(String rideId) {
        // Call ride management API to get ride info
        return null;
    }

    /**
     * Create a new ride offer.
     *
     * @param origin starting location
     * @param destination ending location
     * @param departureTime time of departure
     * @param availableSeats number of available seats
     * @param cost cost per passenger (0 for free)
     * @param isPrivate whether the ride is private (invitation only)
     * @return the created ride ID if successful, null otherwise
     */
    public String createRide(String origin, String destination, LocalDateTime departureTime,
                             int availableSeats, double cost, boolean isPrivate) {
        // Call ride management API to create a new ride
        return null;
    }

    /**
     * Create a new Uber ride offer.
     *
     * @param origin starting location
     * @param destination ending location
     * @param departureTime time of departure
     * @param availableSeats number of available seats
     * @param cost cost per passenger (0 for free)
     * @param isPrivate whether the ride is private (invitation only)
     * @return the created ride ID if successful, null otherwise
     */
    public String createUberRide(String origin, String destination, LocalDateTime departureTime,
                             int availableSeats, double cost, boolean isPrivate) {
        // Call ride management API to create a new ride
        return null;
    }

    /**
     * Join an existing ride.
     *
     * @param rideId ID of the ride to join
     * @return true if successfully joined, false otherwise
     */
    public boolean joinRide(String rideId) {
        // Call ride management API to join a ride
        return false;
    }

    /**
     * Edit an existing ride.
     *
     * @param rideId ID of the ride to edit
     * @param origin updated origin location
     * @param destination updated destination location
     * @param departureTime updated departure time
     * @param availableSeats updated number of available seats
     * @param cost updated cost per passenger
     * @param isPrivate updated privacy status
     * @return true if successfully edited, false otherwise
     */
    public boolean editRide(String rideId, String origin, String destination, LocalDateTime departureTime,
                             int availableSeats, double cost, boolean isPrivate) {
        // Call ride management API to edit a ride
        return false;
    }

    /**
     * Cancel participation in a ride.
     *
     * @param rideId ID of the ride to leave
     * @return true if successfully left, false otherwise
     */
    public boolean leaveRide(String rideId) {
        // Call ride management API to leave a ride
        return false;
    }

    /**
     * Accept a ride request.
     *
     * @param rideId ID of the ride to accept request for
     * @param userId ID of the user to accept request for
     * @return true if successfully left, false otherwise
     */
    public boolean acceptRideRequest(String rideId, String userId) {
        // Call ride management API to accept a ride request
        return false;
    }

    /**
     * Reject a ride request.
     *
     * @param rideId ID of the ride to reject request for
     * @param userId ID of the user to reject request for
     * @return true if successfully left, false otherwise
     */
    public boolean rejectRideRequest(String rideId, String userId) {
        // Call ride management API to reject a ride request
        return false;
    }

    /**
     * Send a message in a ride's chat.
     *
     * @param rideId ID of the ride
     * @param message message content
     * @return true if message sent successfully, false otherwise
     */
    public boolean sendMessage(String rideId, String message) {
        // Call chat/messaging API to send a message
        return false;
    }

    /**
     * Get the chat messages for a ride.
     *
     * @param rideId ID of the ride
     * @return list of messages in the chat
     */
    public List<String> getChatMessages(String rideId) {
        // Call chat/messaging API to get messages
        return null;
    }

    /**
     * Data transfer object for ride search results.
     */
    public static class RideSearchResult {
        private String rideId;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private double cost;
        private int availableSeats;
        private String driverUserName;

        // Getters/setters
    }

}

