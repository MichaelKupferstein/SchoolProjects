package edu.yu.da;

import java.util.List;

public class ThereAndBackAgain extends ThereAndBackAgainBase{
    /**
     * Constructor which supplies the start vertex
     *
     * @param startVertex@throws IllegalArgumentException if the pre-condiitions are
     *                           violated
     */
    public ThereAndBackAgain(String startVertex) {
        super(startVertex);
    }

    /**
     * Adds an weighted undirected edge between vertex v and vertex w.  The two
     * vertices must differ from one another, and an edge between the two
     * vertices cannot have been added previously.
     *
     * @param v      specifies a vertex, length must be > 0.
     * @param w      specifies a vertex, length must be > 0.
     * @param weight the edge's weight, must be > 0.
     * @throws IllegalStateException    if doIt() has previously been invoked.
     * @throws IllegalArgumentException if the other pre-conditions are violated.
     */
    @Override
    public void addEdge(String v, String w, double weight) {

    }

    /**
     * Client informs implementation that the graph is fully constructed and
     * that the ThereAndBackAgainBase algorithm should be run on the graph.
     * After the method completes, the client is permitted to invoke the
     * solution's getters.
     * <p>
     * Note: once invoked, the implementation must ignore subsequent calls to
     * this method.
     *
     * @throws IllegalStateException if doIt() has previously been invoked.
     */
    @Override
    public void doIt() {

    }

    /**
     * If the graph contains a "goal vertex of the longest valid path" (as
     * defined by the requirements document), returns it.  Else returns null.
     *
     * @return goal vertex of the longest valid path if one exists, null
     * otherwise.
     */
    @Override
    public String goalVertex() {
        return null;
    }

    /**
     * Returns the cost (sum of the edge weights) of the longest valid path if
     * one exists, 0.0 otherwise.
     *
     * @return the cost if the graph contains a longest valid path, 0.0
     * otherwise.
     */
    @Override
    public double goalCost() {
        return 0;
    }

    /**
     * If a longest valid path exists, returns a ordered sequence of vertices
     * (beginning with the start vertex, and ending with the goal vertex)
     * representing that path.
     * <p>
     * IMPORTANT: given the existence of (by definition) two longest valid paths,
     * this method returns the List with the LESSER of the two List.hashCode()
     * instances.
     *
     * @return one of the two longest paths, Collections.EMPTY_LIST if the graph
     * doesn't contain a longest valid path.
     */
    @Override
    public List<String> getOneLongestPath() {
        return null;
    }

    /**
     * If a longest valid path exists, returns the OTHER ordered sequence of
     * vertices (beginning with the start vertex, and ending with the goal
     * vertex) representing that path.
     * <p>
     * IMPORTANT: given the existence of (by definition) two longest valid paths,
     * this method returns the List with the GREATER of the two List.hashCode()
     * instances.
     *
     * @return the other of the two longest paths, Collections.EMPTY_LIST if the
     * graph doesn't contain a longest valid path.
     */
    @Override
    public List<String> getOtherLongestPath() {
        return null;
    }
}
