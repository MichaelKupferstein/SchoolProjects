package edu.yu.da;

import edu.yu.da.graph.EdgeWeightedDirectedGraph;
import edu.yu.da.graph.DirectedEdge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MinimumSpanningTree {

    private HashMap<String, DirectedEdge> edgeTo;
    private HashMap<String, Double> distTo;
    private HashMap<String, Boolean> marked;
    private IndexMinPQ<Double> pq;
    private String startVertex;
    private String secondStartVertex;
    private Set<String> connectedVertices;
    private EdgeWeightedDirectedGraph graph;

    public MinimumSpanningTree(EdgeWeightedDirectedGraph G, String startVertex, String secondStartVertex){
        this.graph = G;
        edgeTo = new HashMap<>();
        distTo = new HashMap<>();
        marked = new HashMap<>();
        pq = new IndexMinPQ<>(G.V());
        this.startVertex = startVertex;
        this.secondStartVertex = secondStartVertex;
        connectedVertices = new HashSet<>();

        for (String v : G.vertices()) {
            distTo.put(v, Double.POSITIVE_INFINITY);
            marked.put(v, false);
        }

        if(secondStartVertex != null) marked.put(secondStartVertex, true);

        mst(G, startVertex);
        if (secondStartVertex != null && connectedVertices.size() < G.V()) mst(G, secondStartVertex);

    }

    private void mst(EdgeWeightedDirectedGraph G, String s) {
        distTo.put(s, 0.0);
        pq.insert(s, distTo.get(s));
        while (!pq.isEmpty()) {
            String v = pq.delMin();
            scan(G, v);
        }
    }

    private void scan(EdgeWeightedDirectedGraph G, String v) {
        marked.put(v, true);
        connectedVertices.add(v);
        for (DirectedEdge e : G.adj(v)) {
            String w = e.to();
            if (e.weight() < distTo.get(w)) {
                distTo.put(w, e.weight());
                edgeTo.put(w, e);
                if (pq.contains(w)) pq.changeKey(w, distTo.get(w));
                else if (!marked.get(w)) pq.insert(w, distTo.get(w));
            }
        }
    }

    public double getMaxWeightEdge() {
        double maxWeight = 0;
        for(double e : distTo.values()){
            if (e > maxWeight) maxWeight = e;
        }
        return maxWeight;
    }

    public boolean isGraphConnected() {
        return connectedVertices.size() >= graph.V();
    }

}