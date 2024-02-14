package edu.yu.da.graph;

/**
 * Represents an directed edge in a graph.
 * This class is a slightly modified version of the Edge class from
 * https://algs4.cs.princeton.edu/43mst/Edge.java.html to fit the project requirements.
 */
public class DirectedEdge implements Comparable<DirectedEdge>{
    private final String v; // starting vertex
    private final String w; // ending vertex
    private final double weight; // weight of the edge

    /**
     * Initializes a directed edge from vertex {@code v} to vertex {@code w} with
     * the given {@code weight}.
     *
     * @param v the starting vertex
     * @param w the ending vertex
     * @param weight the weight of the edge
     */
    public DirectedEdge(String v, String w, double weight){
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    /**
     * Returns the weight of the directed edge.
     *
     * @return the weight of the directed edge
     */
    public double weight(){
        return weight;
    }

    /**
     * Returns the starting vertex of the directed edge.
     *
     * @return the starting vertex of the directed edge
     */
    public String from(){
        return v;
    }

    /**
     * Returns the ending vertex of the directed edge.
     *
     * @return the ending vertex of the directed edge
     */
    public String to(){
        return w;
    }

    /**
     * Compares two edges by weight.
     *
     * @param that the other edge
     * @return a negative integer, zero, or positive integer as this edge is less than, equal to, or greater than that edge
     */
    public int compareTo(DirectedEdge that){
        if(this.weight < that.weight) return -1;
        else if(this.weight > that.weight) return 1;
        else return 0;
    }

    /**
     * Returns a string representation of the directed edge.
     *
     * @return a string representation of the directed edge
     */
    @Override
    public String toString() {
        return "Edge{" +
                "from='" + v + '\'' +
                ", to='" + w + '\'' +
                ", weight=" + weight +
                '}';
    }
}