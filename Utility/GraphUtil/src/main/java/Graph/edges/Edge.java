package Graph.edges;

import java.util.Objects;

public class Edge implements Comparable<Edge>{
    private final String v;
    private final String w;
    private final double weight;

    /**
     * Constructs an Edge with two vertices and a weight.
     *
     * @param v      the first vertex of the edge
     * @param w      the second vertex of the edge
     * @param weight the weight of the edge
     */
    public Edge(String v, String w, double weight){
        this.v = v;
        this.w = w;
        this.weight = weight;
    }

    /**
     * Returns the weight of the edge.
     *
     * @return the weight of the edge
     */
    public double weight(){
        return weight;
    }

    /**
     * Returns one of the vertices of the edge.
     *
     * @return the first vertex of the edge
     */
    public String either(){
        return v;
    }

    /**
     * Returns the other vertex of the edge.
     *
     * @param vertex one of the vertices of the edge
     * @return the other vertex of the edge
     * @throws RuntimeException if the provided vertex is not one of the vertices of the edge
     */
    public String other(String vertex){
        if(vertex.equals(v)) return w;
        else if(vertex.equals(w)) return v;
        else throw new RuntimeException("Illegal endpoint");
    }

    /**
     * Compares this edge to another edge based on their weights.
     *
     * @param that the other edge to compare to
     * @return a negative integer, zero, or a positive integer as this edge's weight
     *         is less than, equal to, or greater than the specified edge's weight
     */
    public int compareTo(Edge that){
        if(this.weight < that.weight) return -1;
        else if(this.weight > that.weight) return 1;
        else return 0;
    }

    /**
     * Returns a string representation of the edge.
     *
     * @return a string representation of the edge
     */
    @Override
    public String toString() {
        return "Edge{" +
                "v='" + v + '\'' +
                ", w='" + w + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(weight, edge.weight) == 0 && Objects.equals(v, edge.v) && Objects.equals(w, edge.w);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v, w, weight);
    }
}