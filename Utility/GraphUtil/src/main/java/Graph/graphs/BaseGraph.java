package Graph.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseGraph<T> {
    protected String startVertex;
    protected HashMap<String, ArrayList<T>> adj;
    protected Set<T> edges;
    protected Set<String> vertices;


    public BaseGraph(){
        this.edges = new HashSet<>();
        this.adj = new HashMap<>();
        this.vertices = new HashSet<>();
    }

    public BaseGraph(String startVertex){
        if(startVertex == null || startVertex.length() == 0) throw new IllegalArgumentException("Start vertex must be non-null and not empty");
        this.startVertex = startVertex;
        this.edges = new HashSet<>();
        this.adj = new HashMap<>();
        this.vertices = new HashSet<>();
        this.vertices.add(startVertex);

    }

    public String startVertex(){
        if(startVertex == null) return vertices().iterator().next();
        return startVertex;
    }

    public int V(){
        return vertices.size();
    }

    public int E(){
        return edges.size();
    }

    public abstract void addEdge(T e);
    public abstract void addEdge(String v, String w, double weight);
    public abstract Iterable<T> adj(String v);
    public abstract Iterable<T> edges();
    public abstract boolean edgeExists(String v, String w);

    public Iterable<String> vertices(){
        return vertices;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (T e : this.edges()) {
            s.append(e + "\n");
        }
        return s.toString();
    }
}