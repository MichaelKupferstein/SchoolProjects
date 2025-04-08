package edu.yu.da;

import Graph.graphs.EdgeWeightedGraph;

import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The path that you compute must have all of the following properties:
 *
 * • The path must be balanced, defined as “the length of the path from
 *   s to v must be identical to the length of the path from v to s on the
 *   return trip”.
 *
 * • The path must be efficient, defined as “the path from s to v and from
 *   v to s must both be shortest paths” compared to alternative paths
 *   with the same source and destination vertices.
 *
 * • The path from s to v must differ from the path from v to s in at least
 *   one vertex. (The sequence of vertices in the two paths will certainly
 *   differ. This property states that that the two paths also differ in at
 *   least one vertex.)
 *
 * The above properties define a valid path. The problem requires you
 * to compute the longest valid path, defined as “the valid path with maximum
 * total distance compared to alternative valid paths”.
 * */


public class ThereAndBackAgain extends ThereAndBackAgainBase{

    private final EdgeWeightedGraph graph;
    private boolean didIt = false;
    private final String startVertex;
    private String goal;
    private List<String> oneLongestPath, otherLongestPath;
    private double goalCost;


    /** Constructor which supplies the start vertex
     *
     * @param startVertex, length must be > 0.
     * @throws IllegalArgumentException if the pre-conditions are
     * violated
     */
    public ThereAndBackAgain(String startVertex) {
        super(startVertex);
        if(startVertex == null || startVertex.isEmpty()) throw new IllegalArgumentException("Start vertex cannot be null");
        this.startVertex = startVertex;
        graph = new EdgeWeightedGraph();
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
        if (didIt) throw new IllegalStateException("doIt has previously been invoked");
        if(v.equals(w)) throw new IllegalArgumentException("The two vertices must differ from one another");
        if(graph.edgeExists(v,w)) throw new IllegalArgumentException("Edge between " + v + " and " + w +  " already exists");
        if(v.isEmpty() || w.isEmpty()) throw new IllegalArgumentException("Vertex length must be greater that 0");
        graph.addEdge(v,w,weight);
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
    public void doIt(){
        if(didIt) throw new IllegalStateException("doIt() has previously been invoked");
        if(graph.adj(startVertex) == null) {
            didIt = true;
            return;
        }
        Dijkstra dijkstra = new Dijkstra(graph, startVertex);
        PriorityQueue<Vertex> pq = new PriorityQueue<>();
        for(String s : graph.vertices()){
            pq.add(new Vertex(s, dijkstra.distTo(s)));
        }
        while(!pq.isEmpty()){
            Vertex current = pq.poll();
            if(!dijkstra.hasMultiplePath(current.getVertex())) continue;

            goal = current.getVertex();
            goalCost = current.getDist();
            assignPaths(dijkstra.pathTo(goal).get(0),dijkstra.pathTo(goal).get(1));
            break;
        }
        didIt = true;
    }

    private void assignPaths(List<String> path1, List<String> path2){
        if(path1.hashCode() < path2.hashCode()){
            oneLongestPath = path1;
            otherLongestPath = path2;
        }else{
            oneLongestPath = path2;
            otherLongestPath = path1;
        }
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
        if(!didIt) throw new IllegalStateException("doIt() has not been invoked yet");
        return goal;
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
        if(!didIt) throw new IllegalStateException("doIt() has not been invoked yet");
        return goalCost;
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
        if(!didIt) throw new IllegalStateException("doIt() has not been invoked yet");
        if(oneLongestPath == null) return Collections.EMPTY_LIST;
        return oneLongestPath;
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
        if(!didIt) throw new IllegalStateException("doIt() has not been invoked yet");
        if(otherLongestPath == null) return Collections.EMPTY_LIST;
        return otherLongestPath;
    }

    private class Vertex implements Comparable<Vertex>{
        private final String vertex;
        private final double dist;

        public Vertex(String vertex, double dist){
            this.vertex = vertex;
            this.dist = dist;
        }

        public int compareTo(Vertex that){ //want it so that its stored largest to smallest in pq ^^
            if(this.dist > that.getDist()) return -1;
            else if(this.dist < that.getDist()) return 1;
            else return 0;
        }
        public String getVertex(){
            return vertex;
        }
        public double getDist(){
            return dist;
        }
    }
}
