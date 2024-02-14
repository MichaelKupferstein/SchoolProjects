package Tests.Graph.visualizer;

import Graph.edges.DirectedEdge;
import Graph.generator.RandomGraphGenerator;
import Graph.generator.TriFunction;
import Graph.graphs.BaseGraph;
import Graph.graphs.EdgeWeightedDirectedGraph;
import Graph.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;

public class GraphVisualizerTest {

    @Test
    void testRandomGraphGenerator() throws Exception {
        RandomGraphGenerator<DirectedEdge> randomGraphGenerator = new RandomGraphGenerator<>(100, new EdgeWeightedDirectedGraph(), (v, w, weight) -> new DirectedEdge(v, w, weight));
        BaseGraph<DirectedEdge> graph = randomGraphGenerator.generate();
        GraphVisualizer gv = new GraphVisualizer(graph);
        //gv.visualizeGraphWithCircleLayout();
        gv.visualizeGraphWithFastOrganicLayout();
        //gv.visualizeGraphWithHierarchicalLayout();
    }
}