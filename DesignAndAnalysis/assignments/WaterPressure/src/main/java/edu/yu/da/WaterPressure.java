package edu.yu.da;

import edu.yu.da.graph.EdgeWeightedDirectedGraph;

public class WaterPressure extends WaterPressureBase{

    private EdgeWeightedDirectedGraph graph;
    private boolean didIt = false;
    private boolean isSecondInputPump = false;
    private String initialPump, secondInputPump;

    /** Constructor which supplies the initial input pump.
     *
     * @param initialStartPump, length must be greater than 0.
     * @throws IllegalArgumentException if the pre-conditions are violated.
     */
    public WaterPressure(String initialInputPump) {
        super(initialInputPump);
        if(initialInputPump.isEmpty()) throw new IllegalArgumentException("The initial input pump must not be empty.");
        this.graph = new EdgeWeightedDirectedGraph(initialInputPump);
        this.initialPump = initialInputPump;
    }

    /**
     * Adds a second input pump, differing from the initial input pump, to the
     * channel system.
     * <p>
     * The second input pump must already be in the channel system (via
     * addBlockage): this method only designates the pump as also being an input
     * pump.
     *
     * @param secondInputPump
     * @throws IllegalArgumentException if the pre-conditions are violated.
     */
    @Override
    public void addSecondInputPump(String secondInputPump) {
        if(secondInputPump.isEmpty()) throw new IllegalArgumentException("The second input pump must not be empty.");
        if(secondInputPump.equals(initialPump)) throw new IllegalArgumentException("The second input pump must differ from the initial input pump.");
        if(!graph.vertexExists(secondInputPump)) throw new IllegalArgumentException("The second input pump must already be in the channel system.");
        if(this.isSecondInputPump) throw new IllegalStateException("The second input pump has already been added.");
        this.isSecondInputPump = true;
        this.secondInputPump = secondInputPump;

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
        if (didIt) throw new IllegalStateException("minAmount() has previously been invoked.");
        if(v.isEmpty() || w.isEmpty()) throw new IllegalArgumentException("The pump stations must not be empty.");
        if(v.equals(w)) throw new IllegalArgumentException("The two pump stations must differ from one another");
        if(blockage <= 0) throw new IllegalArgumentException("The magnitude of the blockage on the channel, must be > 0.");
        if(graph.edgeExists(v, w)) throw new IllegalArgumentException("No channel can already exist between the two pump stations.");
        graph.addEdge(v, w, blockage);
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
        MinimumSpanningTree mst = new MinimumSpanningTree(this.graph,this.initialPump,this.secondInputPump);
        didIt = true;
        if(mst.isGraphConnected()) return mst.getMaxWeightEdge();
        else return -1;
    }
}
