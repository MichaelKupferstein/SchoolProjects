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

        Dijkstra dijkstra = new Dijkstra(graph);
        String startVertex = "0";
        assertEquals(dijkstra.distTo(startVertex,"0"), 0.0);
        assertEquals(dijkstra.distTo(startVertex,"1"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"2"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"3"), 19.0);
        assertEquals(dijkstra.distTo(startVertex,"4"), 22.0);
        assertEquals(dijkstra.distTo(startVertex,"5"), 16.0);
        assertEquals(dijkstra.distTo(startVertex,"6"), 16.0);
        assertEquals(dijkstra.distTo(startVertex,"7"), 18.0);

        assertEquals(dijkstra.pathTo(startVertex,"0").toString(), "[0]");
        assertEquals(dijkstra.pathTo(startVertex,"1").toString(), "[0, 1]");
        assertEquals(dijkstra.pathTo(startVertex,"2").toString(), "[0, 2]");
        assertEquals(dijkstra.pathTo(startVertex,"3").toString(), "[0, 2, 5, 3]");
        assertEquals(dijkstra.pathTo(startVertex,"4").toString(), "[0, 2, 5, 7, 4]");
        assertEquals(dijkstra.pathTo(startVertex,"5").toString(), "[0, 2, 5]");
        assertEquals(dijkstra.pathTo(startVertex,"6").toString(), "[0, 1, 6]");
        assertEquals(dijkstra.pathTo(startVertex,"7").toString(), "[0, 2, 5, 7]");
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

        Dijkstra dijkstra = new Dijkstra(graph);
        String startVertex = "0";
        assertEquals(dijkstra.distTo(startVertex,"0"), 0.0);
        assertEquals(dijkstra.distTo(startVertex,"1"), 8.0);
        assertEquals(dijkstra.distTo(startVertex,"2"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"3"), 2.0);
        assertEquals(dijkstra.distTo(startVertex,"4"), 6.0);
        assertEquals(dijkstra.distTo(startVertex,"5"), 10.0);
        assertEquals(dijkstra.distTo(startVertex,"6"), 9.0);

        assertEquals(dijkstra.pathTo(startVertex,"0").toString(), "[0]");
        assertEquals(dijkstra.pathTo(startVertex,"1").toString(), "[0, 1]");
        assertEquals(dijkstra.pathTo(startVertex,"2").toString(), "[0, 2]");
        assertEquals(dijkstra.pathTo(startVertex,"3").toString(), "[0, 3]");
        assertEquals(dijkstra.pathTo(startVertex,"4").toString(), "[0, 4]");
        assertEquals(dijkstra.pathTo(startVertex,"5").toString(), "[0, 3, 5]");
        assertEquals(dijkstra.pathTo(startVertex,"6").toString(), "[0, 4, 6]");
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

        Dijkstra dijkstra = new Dijkstra(graph);
        String startVertex = "0";
        assertEquals(dijkstra.distTo(startVertex,"0"), 0.0);
        assertEquals(dijkstra.distTo(startVertex,"1"), 5.0);
        assertEquals(dijkstra.distTo(startVertex,"2"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"3"), 9.0);
        assertEquals(dijkstra.distTo(startVertex,"4"), 16.0);
        assertEquals(dijkstra.distTo(startVertex,"5"), 9.0);
        assertEquals(dijkstra.distTo(startVertex,"6"), 17.0);
        assertEquals(dijkstra.distTo(startVertex,"7"), 12.0);

        assertEquals(dijkstra.pathTo(startVertex,"0").toString(), "[0]");
        assertEquals(dijkstra.pathTo(startVertex,"1").toString(), "[0, 1]");
        assertEquals(dijkstra.pathTo(startVertex,"2").toString(), "[0, 1, 2]");
        assertEquals(dijkstra.pathTo(startVertex,"3").toString(), "[0, 1, 3]");
        assertEquals(dijkstra.pathTo(startVertex,"4").toString(), "[0, 1, 2, 4]");
        assertEquals(dijkstra.pathTo(startVertex,"5").toString(), "[0, 1, 5]");
        assertEquals(dijkstra.pathTo(startVertex,"6").toString(), "[0, 1, 5, 7, 6]");
        assertEquals(dijkstra.pathTo(startVertex,"7").toString(), "[0, 1, 5, 7]");

        startVertex = "5";
        assertEquals(dijkstra.distTo(startVertex,"0"), 9.0);
        assertEquals(dijkstra.distTo(startVertex,"1"), 4.0);
        assertEquals(dijkstra.distTo(startVertex,"2"), 6.0);
        assertEquals(dijkstra.distTo(startVertex,"3"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"4"), 10.0);
        assertEquals(dijkstra.distTo(startVertex,"5"), 0.0);
        assertEquals(dijkstra.distTo(startVertex,"6"), 8.0);
        assertEquals(dijkstra.distTo(startVertex,"7"), 3.0);

        assertEquals(dijkstra.pathTo(startVertex,"0").toString(), "[5, 1, 0]");
        assertEquals(dijkstra.pathTo(startVertex,"1").toString(), "[5, 1]");
        assertEquals(dijkstra.pathTo(startVertex,"2").toString(), "[5, 1, 2]");
        assertEquals(dijkstra.pathTo(startVertex,"3").toString(), "[5, 7, 3]");
        assertEquals(dijkstra.pathTo(startVertex,"4").toString(), "[5, 7, 4]");
        assertEquals(dijkstra.pathTo(startVertex,"5").toString(), "[5]");
        assertEquals(dijkstra.pathTo(startVertex,"6").toString(), "[5, 7, 6]");
        assertEquals(dijkstra.pathTo(startVertex,"7").toString(), "[5, 7]");

        startVertex = "2";
        assertEquals(dijkstra.distTo(startVertex,"0"), 7.0);
        assertEquals(dijkstra.distTo(startVertex,"1"), 2.0);
        assertEquals(dijkstra.distTo(startVertex,"2"), 0.0);
        assertEquals(dijkstra.distTo(startVertex,"3"), 6.0);
        assertEquals(dijkstra.distTo(startVertex,"4"), 9.0);
        assertEquals(dijkstra.distTo(startVertex,"5"), 6.0);
        assertEquals(dijkstra.distTo(startVertex,"6"), 14.0);
        assertEquals(dijkstra.distTo(startVertex,"7"), 9.0);

        assertEquals(dijkstra.pathTo(startVertex,"0").toString(), "[2, 1, 0]");
        assertEquals(dijkstra.pathTo(startVertex,"1").toString(), "[2, 1]");
        assertEquals(dijkstra.pathTo(startVertex,"2").toString(), "[2]");
        assertEquals(dijkstra.pathTo(startVertex,"3").toString(), "[2, 1, 3]");
        assertEquals(dijkstra.pathTo(startVertex,"4").toString(), "[2, 4]");
        assertEquals(dijkstra.pathTo(startVertex,"5").toString(), "[2, 1, 5]");
        assertEquals(dijkstra.pathTo(startVertex,"6").toString(), "[2, 1, 5, 7, 6]");
        assertEquals(dijkstra.pathTo(startVertex,"7").toString(), "[2, 1, 5, 7]");

        assertEquals(dijkstra.distTo("0","4"), 16.0);
        assertEquals(dijkstra.pathTo("0","4").toString(), "[0, 1, 2, 4]");



    }
}
