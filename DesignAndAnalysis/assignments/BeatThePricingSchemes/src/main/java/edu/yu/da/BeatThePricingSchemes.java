package edu.yu.da;

import java.util.List;

/**
 * See notes above the base class.
 * */

public class BeatThePricingSchemes extends BeatThePricingSchemesBase{
    /**
     * Constructor: client specifies the price of a single quantity of the
     * desired item.
     *
     * @param unitPrice the price-per-single-unit, must be greater than 0.
     * @throw IllegalArgumentException if the parameter pre-conditions are
     * violated.
     */
    public BeatThePricingSchemes(double unitPrice) {
        super(unitPrice);
    }

    /**
     * Adds a pricing scheme to be considered when making the "select optimal
     * pricing schemes" decision.
     *
     * @param price    the price to be paid for the specified quantity, must be
     *                 greater than 0.
     * @param quantity
     * @throw IllegalArgumentException if the parameter pre-conditions are violated.
     * @see MAX_SCHEMES
     */
    @Override
    public void addPricingScheme(double price, int quantity) {

    }

    /**
     * Returns the cheapest price needed to buy at least threshold items.  Thus
     * the quantity bought may exceed the threshold, as long as that is the
     * cheapest price for threshold number of items given the current set of
     * price schemas.
     *
     * @param threshold the minimum number of items to be purchased, cannot
     *                  exceed MAX_MATZOS, and must be greater than zero.
     * @return the cheapest price required to purchase at least the threshold
     * quantity.
     * @throw IllegalArgumentException if the parameter pre-conditions are violated.
     * @see MAX_MATZOS
     */
    @Override
    public double cheapestPrice(int threshold) {
        return 0;
    }

    /**
     * Returns a list of optimal price scheme decisions corresponding to the
     * cheapest price.  If a unit price decision is made, it's represented by the
     * UNIT_PRICE_DECISION constant.  Otherwise, a price scheme is represented by
     * the order in which it was added to this instance: 1..N
     *
     * @see UNIT_PRICE_DECISION
     * @see cheapestPrice
     */
    @Override
    public List<Integer> optimalDecisions() {
        return null;
    }
}
