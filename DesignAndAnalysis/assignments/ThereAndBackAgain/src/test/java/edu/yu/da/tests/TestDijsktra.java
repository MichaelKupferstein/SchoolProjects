package edu.yu.da.tests;
import edu.yu.da.Dijkstra;
import edu.yu.da.EdgeWeightedGraph;

import org.junit.jupiter.api.Test;
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

        Dijkstra dijkstra = new Dijkstra(graph, "0");
        assertEquals(dijkstra.distTo("0"), 0.0);
        assertEquals(dijkstra.distTo("1"), 7.0);
        assertEquals(dijkstra.distTo("2"), 7.0);
        assertEquals(dijkstra.distTo("3"), 19.0);
        assertEquals(dijkstra.distTo("4"), 22.0);
        assertEquals(dijkstra.distTo("5"), 16.0);
        assertEquals(dijkstra.distTo("6"), 16.0);
        assertEquals(dijkstra.distTo("7"), 18.0);

        assertEquals(dijkstra.pathTo("0").toString(), "[0]");
        assertEquals(dijkstra.pathTo("1").toString(), "[0, 1]");
        assertEquals(dijkstra.pathTo("2").toString(), "[0, 2]");
        assertEquals(dijkstra.pathTo("3").toString(), "[0, 2, 5, 3]");
        assertEquals(dijkstra.pathTo("4").toString(), "[0, 2, 5, 7, 4]");
        assertEquals(dijkstra.pathTo("5").toString(), "[0, 2, 5]");
        assertEquals(dijkstra.pathTo("6").toString(), "[0, 1, 6]");
        assertEquals(dijkstra.pathTo("7").toString(), "[0, 2, 5, 7]");
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

        Dijkstra dijkstra = new Dijkstra(graph, "0");
        assertEquals(dijkstra.distTo("0"), 0.0);
        assertEquals(dijkstra.distTo("1"), 8.0);
        assertEquals(dijkstra.distTo("2"), 7.0);
        assertEquals(dijkstra.distTo("3"), 2.0);
        assertEquals(dijkstra.distTo("4"), 6.0);
        assertEquals(dijkstra.distTo("5"), 10.0);
        assertEquals(dijkstra.distTo("6"), 9.0);

        assertEquals(dijkstra.pathTo("0").toString(), "[0]");
        assertEquals(dijkstra.pathTo("1").toString(), "[0, 1]");
        assertEquals(dijkstra.pathTo("2").toString(), "[0, 2]");
        assertEquals(dijkstra.pathTo("3").toString(), "[0, 3]");
        assertEquals(dijkstra.pathTo("4").toString(), "[0, 4]");
        assertEquals(dijkstra.pathTo("5").toString(), "[0, 3, 5]");
        assertEquals(dijkstra.pathTo("6").toString(), "[0, 4, 6]");
    }
}
