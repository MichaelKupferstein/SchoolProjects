package edu.yu.da;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * EdgeWeightedGraph class taken from
 * https://algs4.cs.princeton.edu/43mst/EdgeWeightedGraph.java
 * Slightly modified to fit the project requirments
 * This class is designed to work with vertices that are Strings and edges that are instances of the Edge class.
 */

public class EdgeWeightedGraph {
    private String startVertex;
    private int E;
    private HashMap<String, ArrayList<Edge>> adj;

    /**
     * Default constructor that initializes an empty graph.
     */
    public EdgeWeightedGraph(){
        this.E = 0;
        this.adj = new HashMap<>();
    }

    /**
     * Constructs an EdgeWeightedGraph with a starting vertex.
     *
     * @param startVertex the starting vertex in the graph
     * @throws IllegalArgumentException if the startingVertex is null or empty
     */
    public EdgeWeightedGraph(String startVertex){
        if(startVertex == null || startVertex.length() == 0) throw new IllegalArgumentException("Start vertex must be non-null and not empty");
        this.startVertex = startVertex;
        this.E = 0;
        this.adj = new HashMap<>();
    }


    /**
     * Returns the start vertex of the graph.
     *
     * @return the start vertex of the graph
     */
    public String startVertex(){
        return startVertex;
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the number of vertices in the graph
     */
    public int V(){
        return adj.size();
    }

    /**
     * Returns the number of edges in the graph.
     *
     * @return the number of edges in the graph
     */
    public int E(){
        return E;
    }

    /**
     * Checks if an edge exists between two vertices in the graph.
     *
     * @param v the first vertex
     * @param w the second vertex
     * @return true if an edge exists between the two vertices, false otherwise
     */
    public boolean edgeExists(String v, String w) {
        if (adj.containsKey(v) && adj.containsKey(w)) {
            for (Edge e : adj.get(v)) {
                if (e.other(v).equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds an edge to the graph.
     *
     * @param e the edge to be added to the graph
     */
    public void addEdge(Edge e){
        String v = e.either();
        String w = e.other(v);
        adj.putIfAbsent(v, new ArrayList<>());
        adj.putIfAbsent(w, new ArrayList<>());
        adj.get(v).add(e);
        adj.get(w).add(e);
        E++;
    }

    /**
     * Adds an edge to the graph.
     *
     * @param v the first vertex of the edge
     * @param w the second vertex of the edge
     * @param weight the weight of the edge
     */
    public void addEdge(String v, String w, double weight){
        Edge e = new Edge(v, w, weight);
        addEdge(e);
    }

    /**
     * Returns an iterable of all edges adjacent to a given vertex.
     *
     * @param v the vertex whose adjacent edges are to be returned
     * @return an iterable of all edges adjacent to the given vertex
     */
    public Iterable<Edge> adj(String v){
        return adj.get(v);
    }

    /**
     * Returns an iterable of all edges in the graph.
     * Each edge is only included once, even though it's stored twice in the adjacency list.
     *
     * @return an iterable of all edges in the graph
     */
    public Iterable<Edge> edges(){
        ArrayList<Edge> list = new ArrayList<>();
        for(String v : adj.keySet()){
            for(Edge e : adj.get(v)){
                String w = e.other(v);
                if (v.compareTo(w) < 0) {
                    list.add(e);
                }
            }
        }
        return list;
    }

    public Iterable<String> vertices(){
        return this.adj.keySet();
    }

    /**
     * Returns a string representation of the EdgeWeightedGraph.
     * The string representation includes a list of all edges in the graph.
     *
     * @return a string representation of the EdgeWeightedGraph
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Edge e : this.edges()) {
            s.append(e + "\n");
        }
        return s.toString();
    }
}