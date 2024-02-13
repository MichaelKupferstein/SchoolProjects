package edu.yu.da.test;


import edu.yu.da.graph.EdgeWeightedDirectedGraph;
import edu.yu.da.test.utils.visualizer.GraphVisualizer;
import edu.yu.da.tests.RandomGraphGenerator;


import org.junit.jupiter.api.Test;

class EdgeWeightedDirectedGraphTest {

    @Test
    void testRandomGraphGenerator() throws Exception {
        RandomGraphGenerator randomGraphGenerator = new RandomGraphGenerator(100);
        EdgeWeightedDirectedGraph graph = randomGraphGenerator.generate();
        GraphVisualizer gv = new GraphVisualizer(graph);
        //gv.visualizeGraphWithCircleLayout();
        gv.visualizeGraphWithFastOrganicLayout();
        //gv.visualizeGraphWithHierarchicalLayout();
    }


}