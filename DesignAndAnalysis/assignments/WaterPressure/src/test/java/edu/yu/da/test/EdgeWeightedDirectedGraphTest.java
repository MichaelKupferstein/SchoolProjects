package edu.yu.da.test;


import edu.yu.da.graph.DirectedEdge;
import edu.yu.da.graph.EdgeWeightedDirectedGraph;
import edu.yu.da.test.utils.visualizer.GraphVisualizer;
import edu.yu.da.tests.RandomGraphGenerator;


import org.junit.jupiter.api.Test;

import java.util.*;

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

    @Test
    void testWithVerSmallGraph(){
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph("Node 0");
        graph.addEdge("Node 0","Node 1",1.0);
        graph.addEdge("Node 1","Node 2",2.0);
        GraphVisualizer gv = new GraphVisualizer(graph);
        gv.visualizeGraphWithFastOrganicLayout();
    }

    @Test
    void testingSortingEdges(){
        EdgeWeightedDirectedGraph graph = new RandomGraphGenerator(10).generate();
        List<DirectedEdge> edges = new ArrayList<>((Collection) graph.edges());
        edges.sort(Comparator.reverseOrder());
        for(DirectedEdge e : edges){
            System.out.println(e.from() + " -> " + e.to() + " : " + e.weight());
        }

    }


}