package edu.yu.da.graph;

import java.util.Objects;

/**
 * Represents an directed edge in a graph.
 * This class is a slightly modified version of the Edge class from
 * https://algs4.cs.princeton.edu/43mst/Edge.java.html to fit the project requirements.
 */
public class DirectedEdge implements Comparable<DirectedEdge>{
    private final String from; // starting vertex
    private final String to; // ending vertex
    private final double weight; // weight of the edge

    /**
     * Initializes a directed edge from vertex from to vertex to with
     * the given weight.
     *
     * @param from the starting vertex
     * @param to the ending vertex
     * @param weight the weight of the edge
     */
    public DirectedEdge(String from, String to, double weight){
        this.from = from;
        this.to = to;
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
        return from;
    }

    /**
     * Returns the ending vertex of the directed edge.
     *
     * @return the ending vertex of the directed edge
     */
    public String to(){
        return to;
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
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", weight=" + weight +
                '}';
    }


    /**
     * Compares this edge to the specified object.
     * @param o the object to compare this edge against
     * @return true if the given object represents a directed edge that is equivalent to this edge, false otherwise
     * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectedEdge that = (DirectedEdge) o;
        return Double.compare(weight, that.weight) == 0 && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    /**
     * Returns a hash code for this edge.
     * @return a hash code for this edge
     * */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }
}