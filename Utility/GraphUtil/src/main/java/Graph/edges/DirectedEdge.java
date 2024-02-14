package Graph.edges;

public class DirectedEdge extends Edge{
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
        super(v,w,weight);
        this.v = v;
        this.w = w;
        this.weight = weight;
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
