package edu.yu.cs.com3810.skoop.ride.dao;

public interface RideDao {
    /**
     * Get ride by ID.
     *
     * @param rideId the ride's ID
     * @return the ride if found, null otherwise
     */
    Ride getRideById(String rideId);

    /**
     * Save a ride.
     *
     * @param ride the ride to save
     * @return true if saved successfully, false otherwise
     */
    boolean saveRide(Ride ride);

    /**
     * Search for rides matching criteria.
     *
     * @param destination destination pattern
     * @param departureTime earliest departure time
     * @param maxCost maximum cost
     * @param requiredSeats minimum available seats
     * @return list of matching rides
     */
    java.util.List<Ride> searchRides(String destination, java.time.LocalDateTime departureTime,
                                     double maxCost, int requiredSeats);

    public static class Ride {
        private String id;
        private String creatorId;
        private String origin;
        private String destination;
        private java.time.LocalDateTime departureTime;
        private int totalSeats;
        private int availableSeats;
        private double cost;
        private boolean isPrivate;
        private boolean isCanceled;
        private java.util.List<String> participantIds;

        // Getters/setters would be here
    }
}
