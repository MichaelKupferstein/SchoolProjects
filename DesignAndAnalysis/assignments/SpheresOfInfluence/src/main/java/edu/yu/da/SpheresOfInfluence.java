package edu.yu.da;

import java.util.*;

public class SpheresOfInfluence extends SpheresOfInfluenceBase {
    private int maxStrength;
    private int maxRight;
    private Map<String, Influencer> influencerMap;

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
        if (maxStrength <= 0 || maxRight <= 0) throw new IllegalArgumentException("maxStrength and maxRight must be greater than 0");
        this.maxStrength = maxStrength;
        this.maxRight = maxRight;
        this.influencerMap = new HashMap<>();
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
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("id must be non-empty");
        if (xValue < 0) throw new IllegalArgumentException("xValue must be non-negative");
        if (radius <= 0) throw new IllegalArgumentException("radius must be greater than 0");
        if (influencerMap.containsKey(id)) throw new IllegalArgumentException("influencer with id " + id + " already exists");
        if(influencerMap.values().stream().anyMatch(influencer -> influencer.getXValue() == xValue && influencer.getRadius() == radius))
            throw new IllegalArgumentException("influencer with xValue " + xValue + " and radius " + radius + " already exists");
        influencerMap.put(id, new Influencer(id, xValue, radius));

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
        List<Influencer> influencers = new ArrayList<>(influencerMap.values());
        influencers.sort(Comparator.comparingInt(Influencer::getXValue));

        List<String> res = new ArrayList<>();
        int rightmost = 0;

        for (Influencer influencer : influencers) {
            if (influencer.getXValue() - influencer.getRadius() > rightmost) return Collections.emptyList();

            if(influencer.getXValue() + influencer.getRadius() <= maxStrength) continue;

            if (influencer.getXValue() + influencer.getRadius() > rightmost) {
                rightmost = influencer.getXValue() + influencer.getRadius();
                res.add(influencer.getId());
            }
            if (rightmost >= maxRight) break;
        }

        if (rightmost < maxRight) return Collections.emptyList();

        Collections.sort(res);
        return res;
    }
}