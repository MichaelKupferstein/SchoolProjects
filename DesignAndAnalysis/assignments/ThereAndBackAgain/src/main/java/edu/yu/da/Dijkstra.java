package edu.yu.da;

import java.util.*;

public class Dijkstra {
    private EdgeWeightedGraph graph;
    private Map<String, Double> distance; //distance from start
    private Map<String, List<String>> paths;
    private PriorityQueue<String> pq;

    public Dijkstra(EdgeWeightedGraph graph, String startVertex){
        this.graph = graph;
        this.distance = new HashMap<>();
        this.paths = new HashMap<>();
        this.pq = new PriorityQueue<>((String v1, String v2) -> Double.compare(distance.get(v1), distance.get(v2)));

        for(String vertex : graph.vertices()){
            distance.put(vertex, Double.POSITIVE_INFINITY); //Start off by setting all distances to infinity
        }
        distance.put(startVertex, 0.0); //Set distance for startVertex to 0

        List<String> startPath = new ArrayList<>();
        startPath.add(startVertex);
        paths.put(startVertex,startPath);

        pq.add(startVertex);

        while(!pq.isEmpty()){
            String temp = pq.poll();
            for(Edge e: graph.adj(temp)){
                String other = e.other(temp);
                double total = distance.get(temp) + e.weight();
                if(total < distance.get(other)){
                    distance.put(other,total);
                    pq.remove(other);
                    pq.add(other); //need these two lines to update it, there were bugs :(

                    List<String> oldPath = paths.get(temp);
                    List<String> newPath = new ArrayList<>(oldPath);
                    newPath.add(other);
                    paths.put(other,newPath);
                }
            }
        }
    }

    public double distTo(String s){
        return distance.get(s);
    }

    public List<String> pathTo(String s){
        return paths.get(s);
    }
}
