package edu.yu.da;

import java.util.*;

public class Dijkstra {
    private EdgeWeightedGraph graph;
    private Map<String, Map<String, Double>> allDistances;
    private Map<String, Map<String, List<String>>> allPaths;

    public Dijkstra(EdgeWeightedGraph graph){
        this.graph = graph;
        this.allDistances = new HashMap<>();
        this.allPaths = new HashMap<>();
    }

    public void calculateShortestPaths(String vertex){
        if (allDistances.containsKey(vertex)) {
            // Shortest paths from vertex has already been calculated
            return;
        }

        Map<String, Double> distance = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>((String v1, String v2) -> Double.compare(distance.get(v1), distance.get(v2)));

        for(String v : graph.vertices()){
            distance.put(v, Double.POSITIVE_INFINITY);
        }
        distance.put(vertex, 0.0);

        List<String> startPath = new ArrayList<>();
        startPath.add(vertex);
        paths.put(vertex,startPath);

        pq.add(vertex);

        while(!pq.isEmpty()){
            String temp = pq.poll();
            for(Edge e: graph.adj(temp)){
                String other = e.other(temp);
                double total = distance.get(temp) + e.weight();
                if(total < distance.get(other)){
                    distance.put(other,total);
                    pq.remove(other);
                    pq.add(other);

                    List<String> oldPath = paths.get(temp);
                    List<String> newPath = new ArrayList<>(oldPath);
                    newPath.add(other);
                    paths.put(other,newPath);
                }
            }
        }

        //was getting OutOfMemoryError so need to remove vlaues that are infiity
        distance.entrySet().removeIf(entry -> entry.getValue() == Double.POSITIVE_INFINITY);

        allDistances.put(vertex, distance);
        allPaths.put(vertex, paths);
    }

    public double distTo(String startVertex, String endVertex){
        calculateShortestPaths(startVertex);
        if(!allDistances.get(startVertex).containsKey(endVertex)) return -1;
        return allDistances.get(startVertex).get(endVertex);
    }

    public List<String> pathTo(String startVertex, String endVertex){
        calculateShortestPaths(startVertex);
        return allPaths.get(startVertex).get(endVertex);
    }
}