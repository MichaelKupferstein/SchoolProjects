package edu.yu.da.graph;

import edu.yu.da.graph.DirectedEdge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The EdgeWeightedDirectedGraph class represents a directed graph where each edge has a weight.
 * The graph is represented using an adjacency list, which is a HashMap where the keys are the vertices and the values are ArrayLists of DirectedEdges.
 */
public class EdgeWeightedDirectedGraph {
    private String startVertex; // The starting vertex of the graph
    private int E; // The number of edges in the graph
    private HashMap<String, ArrayList<DirectedEdge>> adj; // The adjacency list
    private Set<String> V; // The set of vertices in the graph

    /**
     * Initializes an empty EdgeWeightedDirectedGraph with no vertices and no edges.
     */
    public EdgeWeightedDirectedGraph(){
        this.E = 0;
        this.V = new HashSet<>();
        this.adj = new HashMap<>();
    }

    /**
     * Initializes an EdgeWeightedDirectedGraph with one vertex and no edges.
     *
     * @param startVertex the starting vertex of the graph
     */
    public EdgeWeightedDirectedGraph(String startVertex){
        if(startVertex == null || startVertex.length() == 0) throw new IllegalArgumentException("Start vertex must be non-null and not empty");
        this.startVertex = startVertex;
        this.E = 0;
        this.adj = new HashMap<>();
    }

    /**
     * Returns the starting vertex of the graph.
     *
     * @return the starting vertex of the graph
     */
    public String startVertex(){
        if(startVertex == null) return vertices().iterator().next();
        return startVertex;
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the number of vertices in the graph
     */
    public int V(){
        return V.size();
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
     * Checks if there is an edge from vertex v to vertex w.
     *
     * @param v the starting vertex
     * @param w the ending vertex
     * @return true if there is an edge from vertex v to vertex w, false otherwise
     */
    public boolean edgeExists(String v, String w) {
        if (adj.containsKey(v)) {
            for (DirectedEdge e : adj.get(v)) {
                if (e.to().equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds the directed edge e to the graph.
     *
     * @param e the directed edge
     */
    public void addEdge(DirectedEdge e){
        String v = e.from();
        adj.putIfAbsent(v, new ArrayList<>());
        adj.get(v).add(e);
        V.add(e.from());
        V.add(e.to());
        E++;
    }

    /**
     * Adds a directed edge from vertex v to vertex w with the given weight to the graph.
     *
     * @param v the starting vertex
     * @param w the ending vertex
     * @param weight the weight of the edge
     */
    public void addEdge(String v, String w, double weight){
        DirectedEdge e = new DirectedEdge(v, w, weight);
        addEdge(e);
    }

    /**
     * Returns the edges incident to vertex v.
     *
     * @param v the vertex
     * @return the edges incident to vertex v as an Iterable
     */
    public Iterable<DirectedEdge> adj(String v){
        return adj.get(v);
    }

    /**
     * Returns all edges in the graph.
     *
     * @return all edges in the graph as an Iterable
     */
    public Iterable<DirectedEdge> edges(){
        ArrayList<DirectedEdge> list = new ArrayList<>();
        for(String v : adj.keySet()){
            for(DirectedEdge e : adj.get(v)){
                list.add(e);
            }
        }
        return list;
    }

    /**
     * Returns all vertices in the graph.
     *
     * @return all vertices in the graph as an Iterable
     */
    public Iterable<String> vertices(){
        return this.V;
    }

    /**
     * Returns a string representation of the graph.
     *
     * @return a string representation of the graph
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (DirectedEdge e : this.edges()) {
            s.append(e + "\n");
        }
        return s.toString();
    }


}