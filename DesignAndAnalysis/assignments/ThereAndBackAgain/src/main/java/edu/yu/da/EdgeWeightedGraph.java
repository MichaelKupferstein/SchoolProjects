package edu.yu.da;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * EdgeWeightedGraph class taken from
 * https://algs4.cs.princeton.edu/43mst/EdgeWeightedGraph.java
 * Slightly modified to fit the project requirments
 * This class is designed to work with vertices that are Strings and edges that are instances of the Edge class.
 */

public class EdgeWeightedGraph {
    private final int V;
    private int E;
    private HashMap<String, ArrayList<Edge>> adj;

    /**
     * Constructs an EdgeWeightedGraph with a specified number of vertices.
     *
     * @param V the number of vertices in the graph
     * @throws IllegalArgumentException if the number of vertices is negative
     */
    public EdgeWeightedGraph(int V){
        if(V < 0) throw new IllegalArgumentException("Number of vertices must be non-negative");
        this.V = V;
        this.E = 0;
        adj = new HashMap<>();
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the number of vertices in the graph
     */
    public int V(){
        return V;
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
     *
     * @return an iterable of all edges in the graph
     */
    public Iterable<Edge> edges(){
        ArrayList<Edge> list = new ArrayList<>();
        for(String v : adj.keySet()){
            int selfLoops = 0;
            for(Edge e : adj.get(v)){
                if(!e.other(v).equals(v)){
                    list.add(e);
                }
                else{
                    if(selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
}