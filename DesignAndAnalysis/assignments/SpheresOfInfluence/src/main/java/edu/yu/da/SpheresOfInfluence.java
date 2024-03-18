package edu.yu.da;

import java.util.List;

public class SpheresOfInfluence extends SpheresOfInfluenceBase {
    /**
     * Constructor that defines the MU rectangular 2D plane of student values.
     *
     * @param maxStrength the maximum "strength" value demarcating the MU 2D
     *                    plane in one dimension, must be greater than 0.
     * @param maxRight    the maximum "right religiosity" value (in MU's "left to
     *                    right" spectrum) demarcating the MU 2D plane in the other dimension, must
     *                    be greater than 0.
     */
    public SpheresOfInfluence(int maxStrength, int maxRight) {
        super(maxStrength, maxRight);
    }

    /**
     * Specifies the two characteristics of an influencer.
     *
     * @param id     uniquely identifies the influencer, must be non-empty.
     * @param xValue the influencer's position on the "right-to-left" spectrum,
     *               represents the center of the influencer's sphere of influence.  The
     *               influencer's "strength" value is in the center of the MU rectangular 2D
     *               plane.  Must be a non-negative integer.
     * @param radius demarcates the extent of the influencer's influence, must be
     *               greater than 0.
     * @throws IllegalArgumentException if the Javadoc constraints are violated,
     *                                  including if an influencer with this id has previously been added or if an
     *                                  influencer with a duplicate xValue and radius values has previously been
     *                                  added.
     */
    @Override
    public void addInfluencer(String id, int xValue, int radius) {

    }

    /**
     * Returns the ids in the minimal set of influencers that provide complete
     * coverage of the MU rectangular 2D place.
     *
     * @return a List of the relevant ids, Collection.EMPTY_LIST if no set of the
     * supplied influencers can provide complete coverage.  The ids MUST BE
     * sorted in order of increasing lexicographical order.
     */
    @Override
    public List<String> getMinimalCoverageInfluencers() {
        return null;
    }
}
