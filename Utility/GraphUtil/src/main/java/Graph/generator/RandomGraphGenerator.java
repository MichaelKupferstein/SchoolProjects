package Graph.generator;

import Graph.graphs.BaseGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator<T> {
    private final int edges;
    private final Random random = new Random();
    private final BaseGraph<T> graph;
    private final TriFunction<String, String, Double, T> edgeCreator;

    public RandomGraphGenerator(int edges, BaseGraph<T> graph, TriFunction<String, String, Double, T> edgeCreator) {
        this.edges = edges;
        this.graph = graph;
        this.edgeCreator = edgeCreator;
    }

    public BaseGraph<T> generate() {
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < edges; i++) {
            nodes.add("Node " + i);
        }

        for (int i = 0; i < edges; i++) {
            String v = nodes.get(random.nextInt(nodes.size()));
            String w = nodes.get(random.nextInt(nodes.size()));
            while (v.equals(w) || graph.edgeExists(v,w)) {
                w = nodes.get(random.nextInt(nodes.size()));
            }

            double weight = Math.round(1 + (15 - 1) * random.nextDouble());
            T edge = edgeCreator.apply(v, w, weight);
            graph.addEdge(edge);
        }

        return graph;
    }
}