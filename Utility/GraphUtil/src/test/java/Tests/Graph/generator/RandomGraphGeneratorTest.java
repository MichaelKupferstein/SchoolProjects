package Tests.Graph.generator;

import Graph.edges.DirectedEdge;
import Graph.edges.Edge;
import Graph.graphs.EdgeWeightedDirectedGraph;
import Graph.graphs.EdgeWeightedGraph;
import Graph.generator.RandomGraphGenerator;
import Graph.generator.TriFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomGraphGeneratorTest {

    @Test
    public void testEdgeWeightedGraphGenerator() {
        RandomGraphGenerator<Edge> generator = new RandomGraphGenerator<>(10, new EdgeWeightedGraph(), (v, w, weight) -> new Edge(v, w, weight));
        EdgeWeightedGraph graph = (EdgeWeightedGraph) generator.generate();

        assertEquals(10, graph.E());
        System.out.println(graph);
    }

    @Test
    public void testEdgeWeightedDirectedGraphGenerator() {
        RandomGraphGenerator<DirectedEdge> generator = new RandomGraphGenerator<>(10, new EdgeWeightedDirectedGraph(), (v, w, weight) -> new DirectedEdge(v, w, weight));
        EdgeWeightedDirectedGraph graph = (EdgeWeightedDirectedGraph) generator.generate();

        assertEquals(10, graph.E());
        System.out.println(graph);
    }
}