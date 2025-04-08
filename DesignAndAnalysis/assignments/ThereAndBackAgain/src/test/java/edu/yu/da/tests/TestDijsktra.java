package edu.yu.da.tests;
import Graph.graphs.EdgeWeightedGraph;
import edu.yu.da.Dijkstra;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestDijsktra {

    @Test
    void testWithGraph(){
        EdgeWeightedGraph graph = new EdgeWeightedGraph();
        graph.addEdge("0","1",7.0);
        graph.addEdge("0","2",7.0);
        graph.addEdge("1","6",9.0);
        graph.addEdge("2","5",9.0);
        graph.addEdge("5","3",3.0);
        graph.addEdge("5","7",2.0);
        graph.addEdge("3","7",6.0);
        graph.addEdge("7","4",4.0);

        String startVertex = "0";
        Dijkstra dijkstra = new Dijkstra(graph, startVertex);
        assertEquals(0.0, dijkstra.distTo("0"));
        assertEquals(7.0, dijkstra.distTo("1"));
        assertEquals(7.0, dijkstra.distTo("2"));
        assertEquals(19.0, dijkstra.distTo("3"));
        assertEquals(22.0, dijkstra.distTo("4"));
        assertEquals(16.0, dijkstra.distTo("5"));
        assertEquals(16.0, dijkstra.distTo("6"));
        assertEquals(18.0, dijkstra.distTo("7"));

        assertEquals("[0]", dijkstra.pathTo("0").toString());
        assertEquals("[0, 1]", dijkstra.pathTo("1").toString());
        assertEquals("[0, 2]", dijkstra.pathTo("2").toString());
        assertEquals("[0, 2, 5, 3]", dijkstra.pathTo("3").toString());
        assertEquals("[0, 2, 5, 7, 4]", dijkstra.pathTo("4").toString());
        assertEquals("[0, 2, 5]", dijkstra.pathTo("5").toString());
        assertEquals("[0, 1, 6]", dijkstra.pathTo("6").toString());
        assertEquals("[0, 2, 5, 7]", dijkstra.pathTo("7").toString());
    }

    @Test
    void anotherGraph(){
        EdgeWeightedGraph graph = new EdgeWeightedGraph();
        graph.addEdge("0","1",8.0);
        graph.addEdge("0","2",7.0);
        graph.addEdge("0","3",2.0);
        graph.addEdge("0","4",6.0);
        graph.addEdge("1","6",6.0);
        graph.addEdge("2","4",5.0);
        graph.addEdge("2","6",4.0);
        graph.addEdge("3","5",8.0);
        graph.addEdge("4","6",3.0);
        graph.addEdge("5","6",2.0);

        String startVertex = "0";
        Dijkstra dijkstra = new Dijkstra(graph, startVertex);
        assertEquals(0.0, dijkstra.distTo("0"));
        assertEquals(8.0, dijkstra.distTo("1"));
        assertEquals(7.0, dijkstra.distTo("2"));
        assertEquals(2.0, dijkstra.distTo("3"));
        assertEquals(6.0, dijkstra.distTo("4"));
        assertEquals(10.0, dijkstra.distTo("5"));
        assertEquals(9.0, dijkstra.distTo("6"));

        assertEquals("[0]", dijkstra.pathTo("0").toString());
        assertEquals("[0, 1]", dijkstra.pathTo("1").toString());
        assertEquals("[0, 2]", dijkstra.pathTo("2").toString());
        assertEquals("[0, 3]", dijkstra.pathTo("3").toString());
        assertEquals("[0, 4]", dijkstra.pathTo("4").toString());
        assertEquals("[0, 3, 5]", dijkstra.pathTo("5").toString());
        assertEquals("[0, 4, 6]", dijkstra.pathTo("6").toString());
    }

    @Test
    void DifferentStarts(){
        EdgeWeightedGraph graph = new EdgeWeightedGraph();
        graph.addEdge("0","1",5.0);
        graph.addEdge("1","2",2.0);
        graph.addEdge("1","3",4.0);
        graph.addEdge("1","5",4.0);
        graph.addEdge("2","4",9.0);
        graph.addEdge("2","5",7.0);
        graph.addEdge("3","7",4.0);
        graph.addEdge("4","7",7.0);
        graph.addEdge("5","7",3.0);
        graph.addEdge("6","7",5.0);

        String startVertex = "0";
        Dijkstra dijkstra = new Dijkstra(graph, startVertex);
        assertEquals(0.0, dijkstra.distTo("0"));
        assertEquals(5.0, dijkstra.distTo("1"));
        assertEquals(7.0, dijkstra.distTo("2"));
        assertEquals(9.0, dijkstra.distTo("3"));
        assertEquals(16.0, dijkstra.distTo("4"));
        assertEquals(9.0, dijkstra.distTo("5"));
        assertEquals(17.0, dijkstra.distTo("6"));
        assertEquals(12.0, dijkstra.distTo("7"));

        assertEquals("[0]", dijkstra.pathTo("0").toString());
        assertEquals("[0, 1]", dijkstra.pathTo("1").toString());
        assertEquals("[0, 1, 2]", dijkstra.pathTo("2").toString());
        assertEquals("[0, 1, 3]", dijkstra.pathTo("3").toString());
        assertEquals("[0, 1, 2, 4]", dijkstra.pathTo("4").toString());
        assertEquals("[0, 1, 5]", dijkstra.pathTo("5").toString());
        assertEquals("[0, 1, 5, 7, 6]", dijkstra.pathTo("6").toString());
        assertEquals("[0, 1, 5, 7]", dijkstra.pathTo("7").toString());

        startVertex = "5";
        dijkstra = new Dijkstra(graph, startVertex);
        assertEquals(9.0, dijkstra.distTo("0"));
        assertEquals(4.0, dijkstra.distTo("1"));
        assertEquals(6.0, dijkstra.distTo("2"));
        assertEquals(7.0, dijkstra.distTo("3"));
        assertEquals(10.0, dijkstra.distTo("4"));
        assertEquals(0.0, dijkstra.distTo("5"));
        assertEquals(8.0, dijkstra.distTo("6"));
        assertEquals(3.0, dijkstra.distTo("7"));

        assertEquals("[5, 1, 0]", dijkstra.pathTo("0").toString());
        assertEquals("[5, 1]", dijkstra.pathTo("1").toString());
        assertEquals("[5, 1, 2]", dijkstra.pathTo("2").toString());
        assertEquals("[5, 7, 3]", dijkstra.pathTo("3").toString());
        assertEquals("[5, 7, 4]", dijkstra.pathTo("4").toString());
        assertEquals("[5]", dijkstra.pathTo("5").toString());
        assertEquals("[5, 7, 6]", dijkstra.pathTo("6").toString());
        assertEquals("[5, 7]", dijkstra.pathTo("7").toString());

        startVertex = "2";
        dijkstra = new Dijkstra(graph, startVertex);
        assertEquals(7.0, dijkstra.distTo("0"));
        assertEquals(2.0, dijkstra.distTo("1"));
        assertEquals(0.0, dijkstra.distTo("2"));
        assertEquals(6.0, dijkstra.distTo("3"));
        assertEquals(9.0, dijkstra.distTo("4"));
        assertEquals(6.0, dijkstra.distTo("5"));
        assertEquals(14.0, dijkstra.distTo("6"));
        assertEquals(9.0, dijkstra.distTo("7"));

        assertEquals("[2, 1, 0]", dijkstra.pathTo("0").toString());
        assertEquals("[2, 1]", dijkstra.pathTo("1").toString());
        assertEquals("[2]", dijkstra.pathTo("2").toString());
        assertEquals("[2, 1, 3]", dijkstra.pathTo("3").toString());
        assertEquals("[2, 4]", dijkstra.pathTo("4").toString());
        assertEquals("[2, 1, 5]", dijkstra.pathTo("5").toString());
        assertEquals("[2, 1, 5, 7, 6]", dijkstra.pathTo("6").toString());
        assertEquals("[2, 1, 5, 7]", dijkstra.pathTo("7").toString());


    }
}