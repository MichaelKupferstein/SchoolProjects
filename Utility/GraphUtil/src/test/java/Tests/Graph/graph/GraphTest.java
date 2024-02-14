package Tests.Graph.graph;

import Graph.edges.DirectedEdge;
import Graph.edges.Edge;
import Graph.graphs.EdgeWeightedDirectedGraph;
import Graph.graphs.EdgeWeightedGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    public void testEdgeWeightedGraph() {
        EdgeWeightedGraph graph = new EdgeWeightedGraph("A");
        Edge edge = new Edge("A", "B", 1.0);
        graph.addEdge(edge);

        assertEquals(1, graph.E());
        assertEquals(2, graph.V());
        assertTrue(graph.adj("A").iterator().next().equals(edge));
        assertTrue(graph.adj("B").iterator().next().equals(edge));
    }

    @Test
    public void testEdgeWeightedDirectedGraph() {
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph("A");
        DirectedEdge edge = new DirectedEdge("A", "B", 1.0);
        graph.addEdge(edge);

        assertEquals(1, graph.E());
        assertEquals(2, graph.V());
        assertTrue(graph.adj("A").iterator().next().equals(edge));
    }
}