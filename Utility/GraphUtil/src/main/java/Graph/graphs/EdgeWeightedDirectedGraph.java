package Graph.graphs;

import Graph.edges.DirectedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EdgeWeightedDirectedGraph extends BaseGraph<DirectedEdge> {

    public EdgeWeightedDirectedGraph(){
        super();
        this.edges = new HashSet<>();
    }

    public EdgeWeightedDirectedGraph(String startVertex){
        super(startVertex);
        this.edges = new HashSet<>();
    }

    @Override
    public void addEdge(DirectedEdge e){
        String v = e.from();
        adj.putIfAbsent(v, new ArrayList<>());
        adj.get(v).add(e);
        edges.add(e);
        edges.add(e);
        vertices.add(v);
        vertices.add(e.to());
    }
    @Override
    public void addEdge(String v, String w, double weight){
        DirectedEdge e = new DirectedEdge(v, w, weight);
        addEdge(e);
    }

    @Override
    public Iterable<DirectedEdge> adj(String v){
        return adj.get(v);
    }

    @Override
    public Iterable<DirectedEdge> edges(){
        return this.edges;
    }

    public boolean vertexExists(String v) {
        return adj.containsKey(v);
    }

    @Override
    public boolean edgeExists(String v, String w) {
        if (adj.containsKey(v)) {
            for (DirectedEdge e : adj.get(v)) {
                if (e.to().equals(w)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<DirectedEdge> getEdges(){
        return this.edges;
    }
}