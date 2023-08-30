package edu.yu.introtoalgs;

public class OctopusCount implements OctopusCountI{
    /**
     * A single octopus observation, consisting of a set of arrays (each of size
     * exactly N_ARMS), such that the ith element of each array describes the
     * characteristics of the ith arm of the observed octopus.
     *
     * @param observationId non-negative integer, uniquely labels the observation
     *                      (multiple observations can map to the same octopus),
     * @param colors        the color of the ith arm, not null, elements can't be null
     * @param lengthInCM    the length of the ith arm, not null, elements must be
     *                      positive integers
     * @param textures      the texture of the ith arm, not null, elements can't be
     *                      null
     * @throws IllegalArgumentException if any of the parameter conditions are
     *                                  violated: e.g., there aren't exactly N_ARMS values for each arm
     *                                  characteristic or if a lengthInCM value is not a positive integer.
     */
    @Override
    public void addObservation(int observationId, ArmColor[] colors, int[] lengthInCM, ArmTexture[] textures) {

    }

    /**
     * Returns the number of unique octopus instances from the set of current
     * observations.
     *
     * @return the number of unique instances.
     */
    @Override
    public int countThem() {
        return 0;
    }
}
