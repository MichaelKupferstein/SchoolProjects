package edu.yu.da;

import edu.yu.da.nf.EdmondsKarpAdjacencyList;
import edu.yu.da.nf.NetworkFlowSolverBase;
import edu.yu.da.nf.NetworkFlowSolverBase.Edge;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatzoDistribution extends MatzoDistributionBase{

    private Map<String, Integer> stringIntegerMap;
    private int counter = 0;
    private NetworkFlowSolverBase networkFlowSolverBase;
    private List<Edge> edges;
    private String key;


    /**
     * Constructor: defines the two "endpoints" of the distribution network.
     *
     * @param sourceWarehouse      names the warehouse that initiates the matzo
     *                             distribution, cannot be blank, must differ from destinationWarehouse.
     * @param sourceConstraint     positive-valued-integer representing an upper
     *                             bound on the amount of matzo packages that can be distributed per day from
     *                             the source warehouse.
     * @param destinationWarehouse names the warehouse to which all matzos must
     *                             ultimately be delivered, cannot be blank, must differ from sourceWarehouse.
     * @throws IllegalArgumentException if the parameter pre-conditions are not
     *                                  met.
     */
    public MatzoDistribution(String sourceWarehouse, int sourceConstraint, String destinationWarehouse) {
        super(sourceWarehouse, sourceConstraint, destinationWarehouse);
        if(sourceWarehouse.isEmpty() || destinationWarehouse.isEmpty() || sourceWarehouse.equals(destinationWarehouse)) throw new IllegalArgumentException("sourceWarehouse and destinationWarehouse cannot be empty or equal");
        if(sourceConstraint <= 0) throw new IllegalArgumentException("sourceConstraint must be positive");

        this.key = "z$123&?";
        this.stringIntegerMap = new HashMap<>();
        this.edges = new ArrayList<>();
        stringIntegerMap.put(sourceWarehouse, counter++);
        stringIntegerMap.put(sourceWarehouse + key, counter++);
        edges.add(new Edge(stringIntegerMap.get(sourceWarehouse), stringIntegerMap.get(sourceWarehouse + key), sourceConstraint));
        stringIntegerMap.put(destinationWarehouse, counter++);


    }

    /**
     * Adds a warehouse to the distribution network.
     *
     * @param warehouseId uniquely identifies the warehouse, cannot previously
     *                    have been added to the network, cannot be "blank".
     * @param constraint  positive-valued-integer representing an upper bound on
     *                    the amount of matzo packages that can be distributed per day from that
     *                    warehouse.
     * @throws IllegalArgumentException if the parameter pre-conditions are not
     *                                  met.
     */
    @Override
    public void addWarehouse(String warehouseId, int constraint) {
        if(warehouseId.isEmpty()) throw new IllegalArgumentException("warehouseId cannot be empty");
        if(constraint <= 0) throw new IllegalArgumentException("constraint must be positive");
        if(stringIntegerMap.containsKey(warehouseId)) throw new IllegalArgumentException("warehouseId already exists");

        stringIntegerMap.put(warehouseId, counter++);
        stringIntegerMap.put(warehouseId + key, counter++);
        edges.add(new Edge(stringIntegerMap.get(warehouseId), stringIntegerMap.get(warehouseId + key), constraint));

    }

    /**
     * Specify that a road exists from warehouse1 to warehouse2 with a
     * constraint on the capacity of the road.
     *
     * @param w1         warehouse 1, must have already been added to the distribution
     *                   network, different from w2, cannot be blank.
     * @param w2         warehouse 2, must have already been added to the distribution
     *                   network, different from w1, cannot be blank.
     * @param constraint positive-valued-integer, representing an upper bound on
     *                   the amount of matzo packages per day that can be distributed on this road.
     */
    @Override
    public void roadExists(String w1, String w2, int constraint) {
        if(w1.isEmpty() || w2.isEmpty()) throw new IllegalArgumentException("w1 and w2 cannot be empty");
        if(!stringIntegerMap.containsKey(w1) || !stringIntegerMap.containsKey(w2)) throw new IllegalArgumentException("w1 and w2 must be added to the network");
        if(w1.equals(w2)) throw new IllegalArgumentException("w1 and w2 cannot be equal");
        if(w1.equals("t") || w2.equals("s")) throw new IllegalArgumentException("w1 and w2 cannot be source or destination");
        if(constraint <= 0) throw new IllegalArgumentException("constraint must be positive");

        edges.add(new Edge(stringIntegerMap.get(w1 + key), stringIntegerMap.get(w2), constraint));
    }

    /**
     * Returns the maximum amount of matzos per day that the source warehouse
     * can deliver to the destination warehouse.
     *
     * @return the maximum per-day amount of matzos that can be distributed given
     * the distribution network's constraints.
     */
    @Override
    public int max() {
        networkFlowSolverBase = new EdmondsKarpAdjacencyList(counter, 0, 2);
        for(Edge edge : edges){
            networkFlowSolverBase.addEdge(edge.from, edge.to, edge.capacity);
        }
        //print graph
//        for(List<Edge> edge : networkFlowSolverBase.getGraph()){
//            for(Edge e : edge){
//                System.out.println(String.format("Edge %s -> %s | flow = %d | capacity = %d " , e.from, e.to, e.flow, e.capacity));
//            }
//        }

        networkFlowSolverBase.solve();
        return (int) networkFlowSolverBase.getMaxFlow();
    }


}
