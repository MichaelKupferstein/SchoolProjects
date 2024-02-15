package edu.yu.da.test;

import edu.yu.da.WaterPressure;
import edu.yu.da.test.utils.visualizer.GraphVisualizer;
import org.junit.jupiter.api.Test;
import edu.yu.da.test.utils.*;
import edu.yu.da.graph.*;

import static org.junit.jupiter.api.Assertions.*;

class WaterPressureTest {

    private boolean visualize = false;

    @Test
    void smallRandomTest() {
       RandomGraphGenerator rgg = new RandomGraphGenerator(10);
       EdgeWeightedDirectedGraph graph = rgg.generate();
       WaterPressure wp = new WaterPressure("Node 0");
       for(DirectedEdge e : graph.edges()) {
           wp.addBlockage(e.from(), e.to(), e.weight());
       }
       //System.out.println(wp.minAmount());
       if(visualize) new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();


    }

    @Test
    void testFromDocWithOneInput(){
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();
        graph.addEdge(new DirectedEdge("Node 0", "Node 1", 1.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 2", 2.0));
        WaterPressure wp = new WaterPressure("Node 0");
        addBlockages(wp, graph);
        assertEquals(2.0, wp.minAmount());
        if(visualize) new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
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
        if(visualize) new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
    }

    @Test
    void specificTest(){
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();
        graph.addEdge(new DirectedEdge("Node 0", "Node 4", 1.0));
        graph.addEdge(new DirectedEdge("Node 4", "Node 7", 4.0));
        graph.addEdge(new DirectedEdge("Node 4", "Node 5", 3.0));
        graph.addEdge(new DirectedEdge("Node 5", "Node 7", 5.0));
        graph.addEdge(new DirectedEdge("Node 5", "Node 1", 6.0));
        graph.addEdge(new DirectedEdge("Node 5", "Node 6", 7.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 2", 3.0));
        graph.addEdge(new DirectedEdge("Node 2", "Node 3", 2.0));
        graph.addEdge(new DirectedEdge("Node 3", "Node 6", 4.0));

        if(visualize) new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
        WaterPressure wp = new WaterPressure("Node 0");
        addBlockages(wp, graph);
        assertEquals(6.0, wp.minAmount());

        wp = new WaterPressure("Node 0");
        graph.addEdge(new DirectedEdge("Node 8", "Node 9", 7.5));
        graph.addEdge(new DirectedEdge("Node 9", "Node 10", 8.0));
        addBlockages(wp, graph);
        wp.addSecondInputPump("Node 8");
        assertEquals(8.0, wp.minAmount());

        wp = new WaterPressure("Node 0");
        graph.addEdge(new DirectedEdge("Node 11", "Node 12", 9.0));
        addBlockages(wp, graph);
        wp.addSecondInputPump("Node 8");
        if(visualize) new GraphVisualizer(graph).visualizeGraphWithFastOrganicLayout();
        assertEquals(-1.0, wp.minAmount());
    }

    @Test
    void testWithSmallGraph() {
        EdgeWeightedDirectedGraph graph = new EdgeWeightedDirectedGraph();
        graph.addEdge(new DirectedEdge("Node 0", "Node 1", 3.0));
        graph.addEdge(new DirectedEdge("Node 0", "Node 3", 7.0));
        graph.addEdge(new DirectedEdge("Node 0", "Node 4", 8.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 2", 1.0));
        graph.addEdge(new DirectedEdge("Node 1", "Node 3", 4.0));
        graph.addEdge(new DirectedEdge("Node 3", "Node 2", 2.0));
        graph.addEdge(new DirectedEdge("Node 4", "Node 3", 3.0));
        WaterPressure wp = new WaterPressure("Node 0");
        addBlockages(wp, graph);
        assertEquals(8.0, wp.minAmount());
        wp.addSecondInputPump("Node 4");
        assertEquals(3.0, wp.minAmount());
    }

    @Test
    void testWaterPressureThrows(){
        assertThrows(IllegalArgumentException.class, () -> new WaterPressure(""));
    }

    @Test
    void testAddSecondInputPumpThrows(){
        WaterPressure wp = new WaterPressure("Node 0");

        assertThrows(IllegalArgumentException.class, () -> wp.addSecondInputPump("")); //cant be emtpy
        assertThrows(IllegalArgumentException.class, () -> wp.addSecondInputPump("Node 0")); // has to differ from initial pump
        assertThrows(IllegalArgumentException.class, () -> wp.addSecondInputPump("Node 1")); //has to be in the graph

        wp.addBlockage("Node 0", "Node 1", 1.0);
        wp.addBlockage("Node 1", "Node 2", 2.0);
        wp.addSecondInputPump("Node 1");
        assertThrows(IllegalStateException.class, () -> wp.addSecondInputPump("Node 2")); //cant add second input pump twice
    }

    @Test
    void testAddBlockageThrows(){
        WaterPressure wp = new WaterPressure("Node 0");
        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("", "Node 1", 1.0)); //cant be empty
        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("Node 0", "", 1.0)); //cant be empty

        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("Node 0", "Node 0", 1.0)); //cant be the same

        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("Node 0", "Node 1", 0.0)); //cant be 0
        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("Node 0", "Node 1", -1.0)); //cant be negative

        wp.addBlockage("Node 0", "Node 1", 1.0);
        assertThrows(IllegalArgumentException.class, () -> wp.addBlockage("Node 0", "Node 1", 1.0)); //cant add the same edge twice

        wp.minAmount();
        assertThrows(IllegalStateException.class, () -> wp.addBlockage("Node 0", "Node 2", 1.0)); //cant add blockage after minAmount has been called
    }

    private void addBlockages(WaterPressure wp, EdgeWeightedDirectedGraph graph) {
        for(DirectedEdge e : graph.edges()) {
            wp.addBlockage(e.from(), e.to(), e.weight());
        }
    }
}