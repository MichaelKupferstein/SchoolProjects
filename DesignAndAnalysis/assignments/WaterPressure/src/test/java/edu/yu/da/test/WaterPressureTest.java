package edu.yu.da.test;

import edu.yu.da.WaterPressure;
import edu.yu.da.test.utils.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;
import edu.yu.da.test.utils.*;
import edu.yu.da.graph.*;

import static org.junit.jupiter.api.Assertions.*;

class WaterPressureTest {

    @Test
    void smallRandomTest() {
       RandomGraphGenerator rgg = new RandomGraphGenerator(10);
       EdgeWeightedDirectedGraph graph = rgg.generate();
       WaterPressure wp = new WaterPressure("Node 0");
       for(DirectedEdge e : graph.edges()) {
           wp.addBlockage(e.from(), e.to(), e.weight());
       }
       System.out.println(wp.minAmount());
       //GraphVisualizer gv = new GraphVisualizer(graph);
       //gv.visualizeGraphWithFastOrganicLayout();
    }

    @Test
    void testFromDocWithOneInput(){
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();
        graph.addEdge(new DirectedEdge("Node 0", "Node 1", 1.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 2", 2.0));
        WaterPressure wp = new WaterPressure("Node 0");
        addBlockages(wp, graph);
        assertEquals(2.0, wp.minAmount());
        //new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
    }
    @Test
    void testFromDocWithTwoInput(){
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();
        graph.addEdge(new DirectedEdge("Node 0", "Node 1", 1.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 2", 2.0));
        WaterPressure wp = new WaterPressure("Node 0");
        addBlockages(wp, graph);
        wp.addSecondInputPump("Node 2");
        assertEquals(1.0, wp.minAmount());
        //new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
    }

    private void addBlockages(WaterPressure wp, EdgeWeightedDirectedGraph graph) {
        for(DirectedEdge e : graph.edges()) {
            wp.addBlockage(e.from(), e.to(), e.weight());
        }
    }
}