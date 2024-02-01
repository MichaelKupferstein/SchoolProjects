package edu.yu.da.tests;

import edu.yu.da.Edge;
import edu.yu.da.EdgeWeightedGraph;
import edu.yu.da.ThereAndBackAgain;
import edu.yu.da.ThereAndBackAgainBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThereAndBackAgainTest {

    @Test
    void TestFromDoc(){
        final String startVertex = "a";
        final ThereAndBackAgainBase taba = new ThereAndBackAgain(startVertex);
        taba.addEdge(startVertex,"b",1.0);
        taba.addEdge("b","c",2.0);
        taba.doIt();
        assertEquals(null,taba.goalVertex(),"goalVertex");
        assertEquals(0.0,taba.goalCost(),"goalCost");
        assertEquals(null,taba.getOneLongestPath());
        assertEquals(null,taba.getOtherLongestPath());
    }

    @Test
    void TestWithGraph(){
        String startVertex = "0";
        ThereAndBackAgainBase taba = new ThereAndBackAgain(startVertex);
        taba.addEdge(startVertex,"1",3.0);
        taba.addEdge(startVertex,"2",2.0);
        taba.addEdge(startVertex,"3",7.0);
        taba.addEdge("1","2",6.0);
        taba.addEdge("1","3",8.0);
        taba.addEdge("1","6",7.0);
        taba.addEdge("2","4",5.0);
        taba.addEdge("3","5",3.0);
        taba.addEdge("3","7",1.0);
        taba.addEdge("4","6",6.0);
        taba.addEdge("5","7",6.0);
        taba.addEdge("6","7",8.0);

        taba.doIt();
        System.out.println(taba.goalVertex());
        System.out.println(taba.goalCost());
        System.out.println(taba.getOneLongestPath());
        System.out.println(taba.getOtherLongestPath());


    }

    @Test
    void TestWithRandomGraph(){
        EdgeWeightedGraph graph = new RandomGraphGenerator(100).generate();
        ThereAndBackAgainBase taba = new ThereAndBackAgain(graph.startVertex());
        for(Edge e: graph.edges()){
            taba.addEdge(e.either(),e.other(e.either()),e.weight());
        }
        taba.doIt();
        List<String> oneLongestPath = taba.getOneLongestPath();
        List<String> otherLongestPath = taba.getOtherLongestPath();
        System.out.println(taba.goalVertex());
        System.out.println(taba.goalCost());
        System.out.println(oneLongestPath);
        System.out.println(otherLongestPath);

        printPath(graph,oneLongestPath,otherLongestPath);
    }

    @Test
    void TestWithRandomLargeGraph(){
        EdgeWeightedGraph graph = new RandomGraphGenerator(100_000).generate();
        ThereAndBackAgainBase taba = new ThereAndBackAgain(graph.startVertex());
        for(Edge e: graph.edges()){
            taba.addEdge(e.either(),e.other(e.either()),e.weight());
        }
        taba.doIt();
        List<String> oneLongestPath = taba.getOneLongestPath();
        List<String> otherLongestPath = taba.getOtherLongestPath();
        System.out.println(taba.goalVertex());
        System.out.println(taba.goalCost());
        System.out.println(oneLongestPath);
        System.out.println(otherLongestPath);

        printPath(graph,oneLongestPath,otherLongestPath);
    }

    @Test
    void TestWith200_000(){
        EdgeWeightedGraph graph = new RandomGraphGenerator(200_000).generate();
        ThereAndBackAgainBase taba = new ThereAndBackAgain(graph.startVertex());
        for(Edge e: graph.edges()){
            taba.addEdge(e.either(),e.other(e.either()),e.weight());
        }
        taba.doIt();
        List<String> oneLongestPath = taba.getOneLongestPath();
        List<String> otherLongestPath = taba.getOtherLongestPath();
        System.out.println(taba.goalVertex());
        System.out.println(taba.goalCost());
        System.out.println(oneLongestPath);
        System.out.println(otherLongestPath);

        printPath(graph,oneLongestPath,otherLongestPath);
    }


    private void printPath(EdgeWeightedGraph graph, List<String> oneLongestPath, List<String> otherLongestPath){
        if(oneLongestPath != null){
            System.out.println("Edges from vertices in the first path:");
            for (int i = 0; i < oneLongestPath.size() - 1; i++) {
                String vertex1 = oneLongestPath.get(i);
                String vertex2 = oneLongestPath.get(i + 1);
                for (Edge edge : graph.adj(vertex1)) {
                    if (edge.other(vertex1).equals(vertex2)) {
                        System.out.println(edge);
                        break;
                    }
                }
            }
        }

        if(otherLongestPath != null){
            System.out.println("Edges from vertices in the second path:");
            for (int i = 0; i < otherLongestPath.size() - 1; i++) {
                String vertex1 = otherLongestPath.get(i);
                String vertex2 = otherLongestPath.get(i + 1);
                for (Edge edge : graph.adj(vertex1)) {
                    if (edge.other(vertex1).equals(vertex2)) {
                        System.out.println(edge);
                        break;
                    }
                }
            }
        }
    }


}