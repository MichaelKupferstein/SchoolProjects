package edu.yu.da;

import java.util.ArrayList;
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

    private EdgeWeightedGraph graph;
    private boolean didIt = false;
    private String startVertex, goal;
    private Dijkstra dijkstra;
    private List<String> oneLongestPath, otherLongestPath;
    private double goalCost;


    /** Constructor which supplies the start vertex
     *
     * @param startVertex, length must be > 0.
     * @throws IllegalArgumentException if the pre-condiitions are
     * violated
     */
    public ThereAndBackAgain(String startVertex) {
        super(startVertex);
        if (startVertex.length() < 0) throw new IllegalArgumentException("Length must be > 0");
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
        if(graph.edgeExists(v,w)) throw new IllegalArgumentException("Edge between " + v + " and " + " already exists");
        if(v.length() <= 0 || w.length() <=0) throw new IllegalArgumentException("Vertex length must be greater that 0");
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
//    @Override
//    public void doIt() {
//        if(didIt) throw new IllegalStateException("doIt() has previously been invoked");
//        //compute path from startVertex to all othher vertices, maybe dijkstra??
//        this.dijkstra = new Dijkstra(graph,startVertex);
//        double longest = 0.0;
//        String farthestVert = null;
//        for(String v : graph.vertices()){
//            double dist = dijkstra.distTo(v);
//            Dijkstra otherDij = new Dijkstra(graph,v);
//            double otherDist = otherDij.distTo(startVertex);
//            List<String> pathToV = dijkstra.pathTo(v);
//            List<String> pathFromV = otherDij.pathTo(startVertex);
//            if(pathToV == null) continue;
//            if(dist == otherDist && dist > longest && !isReverse(pathToV,pathFromV)){
//                longest = dist;
//                farthestVert = v;
//                oneLongestPath = pathToV;
//                otherLongestPath = pathFromV;
//            }
//        }
//        goal = farthestVert;
//        goalCost = longest;
//        didIt = true;
//    }
    @Override
    public void doIt(){
        if(didIt) throw new IllegalStateException("doIt() has previously been invoked");
        this.dijkstra = new Dijkstra(graph);
        dijkstra.calculateShortestPaths(startVertex);
        for(String s : graph.vertices()){
            dijkstra.distTo(startVertex,s);
        }
    }



    private boolean isReverse(List<String> list1, List<String> list2){
        if(list1.size() != list2.size()) return false;
        for(int i = 0, j = list2.size()-1; i < j; i++,j--){
            if(!list1.get(i).equals(list2.get(j))) return false;
        }
        return true;
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
        return otherLongestPath;
    }
}
