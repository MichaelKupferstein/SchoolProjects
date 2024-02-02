package edu.yu.da;

import java.util.*;

public class Dijkstra {
    private final Map<String, Double> distance; //distance from start
    private final Map<String, List<List<String>>> paths;

    /**
     * Constructs a new Dijkstra instance.
     *
     * @param graph an EdgeWeightedGraph
     * @param startVertex the start vertex
     * */
    public Dijkstra(EdgeWeightedGraph graph, String startVertex){
        this.distance = new HashMap<>();
        this.paths = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>((String v1, String v2) -> Double.compare(distance.get(v1), distance.get(v2)));

        for(String vertex : graph.vertices()){
            distance.put(vertex, Double.POSITIVE_INFINITY);
        }
        distance.put(startVertex, 0.0);

        List<List<String>> startPaths = new ArrayList<>();

        List<String> startPath = new ArrayList<>();
        startPath.add(startVertex);

        startPaths.add(startPath);
        paths.put(startVertex,startPaths);

        pq.add(startVertex);

        while(!pq.isEmpty()){
            String currentVertex = pq.poll();
            for(Edge edge: graph.adj(currentVertex)){

                String otherVertex = edge.other(currentVertex);
                double total = distance.get(currentVertex) + edge.weight();

                if(total < distance.get(otherVertex)){
                    distance.put(otherVertex,total);
                    pq.remove(otherVertex);
                    pq.add(otherVertex);

                    List<String> newPath = new ArrayList<>(paths.get(currentVertex).get(0));
                    newPath.add(otherVertex);
                    List<List<String>> newPaths = new ArrayList<>();
                    newPaths.add(newPath);
                    paths.put(otherVertex,newPaths);
                }else if(total == distance.get(otherVertex)){
                    for(List<String> oldPath : paths.get(currentVertex)){
                        List<String> newPath = new ArrayList<>(oldPath);
                        newPath.add(otherVertex);
                        paths.get(otherVertex).add(newPath);
                    }
                }
            }
        }
    }


    /**
     * Returns the shortest distance to a vertex from the start vertex.
     *
     * @param s the vertex
     * @return the shortest distance
     * */
    public double distTo(String s){
        return distance.get(s);
    }

    /**
     * Returns the shortest path(s) to a vertex.
     *
     * @param s the vertex
     * @return the shortest path(s)
     * */
    public List<List<String>> pathTo(String s){
        return paths.get(s);
    }

    /**
     * Checks if there are multiple shortest paths to a vertex.
     *
     * @param s the vertex
     * @return true if there are multiple shortest paths, false otherwise
     * */
    public boolean hasMultiplePath(String s){
        if(paths.get(s) == null ) return false;
        return paths.get(s).size() > 1;
    }
}
