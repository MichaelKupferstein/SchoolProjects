package edu.yu.da;

import java.util.*;

public class SpheresOfInfluence extends SpheresOfInfluenceBase {
    private final int maxStrength;
    private final int maxRight;
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
        double[] intersectionPoints = calculateIntersectionPoint(xValue, maxStrength/2, radius, maxStrength);
        if(intersectionPoints == null) return;
        Influencer temp = new Influencer(id, xValue, radius, intersectionPoints[0], intersectionPoints[1],maxStrength, maxRight);
        if(influencerMap.values().contains(temp))
            throw new IllegalArgumentException("influencer with xValue " + xValue + " and radius " + radius + " already exists");
        influencerMap.put(id, temp);

    }

    private double[] calculateIntersectionPoint(int h, int k, int r, int y){
        //solve for x (x-h)^2 + (y-k)^2 = r^2, h = xValue, k = yValue, r = radius, y = maxStrength

        //check if line intersects circle
        double distance = Math.abs(y - k);
        if(distance > r) return null;

        double dx = Math.sqrt(r * r - distance * distance);
        double x1 = h - dx;
        double x2 = h + dx;

        return new double[]{x1, x2};
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
        influencers.sort(Comparator.reverseOrder());

        List<String> result = new ArrayList<>();

        double left = Double.MAX_VALUE;
        double right = Double.MIN_VALUE;
        for(Influencer influencer : influencers){
            left = Math.min(left, influencer.getLeft());
            right = Math.max(right, influencer.getRight());
            result.add(influencer.getId());

            if(fullyCovered(left, right)){
                Collections.sort(result);
                return result;
            }
        }

        return Collections.EMPTY_LIST;
    }

    private boolean fullyCovered(double left, double right){
        return left <= 0 && right >= maxRight;
    }

}