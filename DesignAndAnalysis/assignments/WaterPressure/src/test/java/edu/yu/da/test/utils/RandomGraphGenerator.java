package edu.yu.da.tests;

import edu.yu.da.EdgeWeightedDirectedGraph;
import edu.yu.da.DirectedEdge;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGraphGenerator {
    private final int edges;
    private final Random random = new Random();

    public RandomGraphGenerator(int edges) {
        this.edges = edges;
    }

    public EdgeWeightedDirectedGraph generate() {
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();

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

            double weight = 1 + (15 - 1) * random.nextDouble();
            graph.addEdge(new DirectedEdge(v, w, Math.round(weight)));
        }

        return graph;
    }
}