package edu.yu.da;

import java.util.*;

/**
 * See notes above the base class.
 * */

public class BeatThePricingSchemes extends BeatThePricingSchemesBase{

    private List<Scheme> schemes; //maybe make a double array instead of Object
    List<Integer> choices;

    /** Constructor: client specifies the price of a single quantity of the
     * desired item.
     *
     * @param unitPrice the price-per-single-unit, must be greater than 0.
     * @throw IllegalArgumentException if the parameter pre-conditions are
     * violated.
     */
    public BeatThePricingSchemes(double unitPrice) {
        super(unitPrice);
        if(unitPrice <= 0) throw new IllegalArgumentException("unitPrice must be greater than 0");
        this.schemes = new ArrayList<>();
        this.schemes.add(new Scheme(unitPrice, 1));
    }

    /** Adds a pricing scheme to be considered when making the "select optimal
     * pricing schemes" decision.
     *
     * @param price the price to be paid for the specified quantity, must be
     * greater than 0.
     * @param quantity, which for the sake of DP, cannot exceed MAX_MATZOS and
     * must be greater than zero.
     * @throw IllegalArgumentException if the parameter pre-conditions are violated.
     * @see MAX_SCHEMES
     */
    @Override
    public void addPricingScheme(double price, int quantity) {
        if(price <= 0) throw new IllegalArgumentException("price must be greater than 0");
        if(quantity <= 0 || quantity > MAX_MATZOS) throw new IllegalArgumentException("quantity must be greater than 0 and less than or equal to MAX_MATZOS");
        if(this.schemes.size() > MAX_SCHEMES) throw new IllegalArgumentException("MAX_SCHEMES reached");
        this.schemes.add(new Scheme(price, quantity));
    }

    /** Returns the cheapest price needed to buy at least threshold items.  Thus
     * the quantity bought may exceed the threshold, as long as that is the
     * cheapest price for threshold number of items given the current set of
     * price schemas.
     *
     * @param threshold the minimum number of items to be purchased, cannot
     * exceed MAX_MATZOS, and must be greater than zero.
     * @return the cheapest price required to purchase at least the threshold
     * quantity.
     * @throw IllegalArgumentException if the parameter pre-conditions are violated.
     * @see MAX_MATZOS
     */
    @Override
    public double cheapestPrice(int threshold) {
        if(threshold <= 0 || threshold > MAX_MATZOS) throw new IllegalArgumentException("threshold must be greater than 0 and less than or equal to MAX_MATZOS");

        int n = schemes.size();
        double[] K = new double[threshold + 1];
        List<List<Integer>> decisions = new ArrayList<>(threshold + 1);
        Arrays.fill(K, Double.MAX_VALUE);
        K[0] = 0;
        decisions.add(0, new ArrayList<>());

        for (int x = 1; x <= threshold; x++) {
            decisions.add(x, new ArrayList<>());
            for (int i = 0; i < n; i++) {
                int quantity = schemes.get(i).quantity;
                double price = schemes.get(i).price;
                double newPrice;
                if(x - quantity <= 0){
                    newPrice = K[0] + price;
                }else{
                    newPrice = K[x - quantity] + price;
                }
                K[x] = Math.min(K[x], newPrice);
                if (newPrice <= K[x]) {
                    K[x] = newPrice;
                    if(x - quantity < 0){
                        decisions.set(x, new ArrayList<>(decisions.get(0)));
                    }else{
                        decisions.set(x, new ArrayList<>(decisions.get(x - quantity)));
                    }
                    decisions.get(x).add(i);
                }
            }
        }

        //System.out.println(Arrays.toString(K));
        //System.out.println(decisions);
        this.choices = decisions.get(threshold);
        return K[threshold];
    }


    /** Returns a list of optimal price scheme decisions corresponding to the
     * cheapest price.  If a unit price decision is made, it's represented by the
     * UNIT_PRICE_DECISION constant.  Otherwise, a price scheme is represented by
     * the order in which it was added to this instance: 1..N
     *
     * @see UNIT_PRICE_DECISION
     * @see cheapestPrice
     */
    @Override
    public List<Integer> optimalDecisions() {
        return this.choices;
    }

    private class Scheme{
        double price;
        int quantity;

        public Scheme(double price, int quantity){
            this.price = price;
            this.quantity = quantity;
        }

    }
}
