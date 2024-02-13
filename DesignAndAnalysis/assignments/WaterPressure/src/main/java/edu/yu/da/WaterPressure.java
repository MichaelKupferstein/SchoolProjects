package edu.yu.da;

public class WaterPressure extends WaterPressureBase{
    /**
     * Constructor which supplies the initial input pump.
     *
     * @param initialInputPump@throws IllegalArgumentException if the pre-conditions are violated.
     */
    public WaterPressure(String initialInputPump) {
        super(initialInputPump);
    }

    /**
     * Adds a second input pump, differing from the initial input pump, to the
     * channel system.
     * <p>
     * The second input pump must already be in the channel system (via
     * addBlockage): this method only designates the pump as also being an input
     * pump.
     *
     * @param secondInputPump@throws IllegalArgumentException if the pre-conditions are violated.
     */
    @Override
    public void addSecondInputPump(String secondInputPump) {

    }

    /**
     * Specifies a blockage on a channel running from pump station v to pump
     * station w.  The presence of a blockage implies that water can only flow on
     * the channel if a quantity of water greater or equal to "blockage" is
     * pumped by pump station v to w.
     * <p>
     * The two pump stations must differ from one another, and no channel can
     * already exist between the two pump stations.
     *
     * @param v        specifies a pump station, length must be > 0.
     * @param w        specifies a pump station, length must be > 0.
     * @param blockage the magnitude of the blockage on the channel, must be > 0.
     * @throws IllegalStateException    if minAmount() has previously been invoked.
     * @throws IllegalArgumentException if the other pre-conditions are violated.
     */
    @Override
    public void addBlockage(String v, String w, double blockage) {

    }

    /**
     * Client asks implementation to determine the minimum amount of water that
     * must be supplied to the initial input pump to ensure that water reaches
     * every pump station in the existing channel system.  If a second input pump
     * has been added to the channel system, the sematics of "minimum amount" is
     * the "minimum amount of water that must be supplied to BOTH input pump
     * stations".
     *
     * @return the minimum amount of water that must be supplied to the input
     * pump(s) to ensure that water reaches every pump station.  If the channel
     * system has been misconfigured such that no amount of water pumped from the
     * input pump stations can get water to all the pump stations, returns -1.0
     * as as sentinel value.
     */
    @Override
    public double minAmount() {
        return 0;
    }
}
