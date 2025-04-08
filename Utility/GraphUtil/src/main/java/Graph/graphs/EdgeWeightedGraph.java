package Graph.graphs;

import Graph.edges.Edge;

import java.util.ArrayList;

public class EdgeWeightedGraph extends BaseGraph<Edge> {

    public EdgeWeightedGraph(){
        super();
    }

    public EdgeWeightedGraph(String startVertex){
        super(startVertex);
    }

    @Override
    public void addEdge(Edge e){
        String v = e.either();
        String w = e.other(v);
        adj.putIfAbsent(v, new ArrayList<>());
        adj.putIfAbsent(w, new ArrayList<>());
        adj.get(v).add(e);
        adj.get(w).add(e);
        edges.add(e);
        vertices.add(v);
        vertices.add(w);
    }
    @Override
    public void addEdge(String v, String w, double weight){
        Edge e = new Edge(v, w, weight);
        addEdge(e);
    }

    @Override
    public Iterable<Edge> adj(String v){
        return adj.get(v);
    }

    @Override
    public boolean edgeExists(String v, String w) {
        if (adj.containsKey(v)) {
            for (Edge e : adj.get(v)) {
                if (e.other(v).equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Iterable<Edge> edges(){
        ArrayList<Edge> list = new ArrayList<>();
        for(String v : adj.keySet()){
            for(Edge e : adj.get(v)){
                String w = e.other(v);
                if (v.compareTo(w) < 0) {
                    list.add(e);
                }
            }
        }
        return list;
    }
}