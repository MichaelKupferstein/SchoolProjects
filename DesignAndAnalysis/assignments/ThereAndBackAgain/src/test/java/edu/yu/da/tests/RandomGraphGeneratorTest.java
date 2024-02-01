package edu.yu.da.tests;

import edu.yu.da.Dijkstra;
import edu.yu.da.EdgeWeightedGraph;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;


class RandomGraphGeneratorTest {
    @Test
    void generate(){
        EdgeWeightedGraph graph = new RandomGraphGenerator(10).generate();
        System.out.println(graph.toString());
    }

    @Test
    void TestWithDijkstra(){
        EdgeWeightedGraph graph = new RandomGraphGenerator(10).generate();
        //new GraphVisualizer(graph);

        //GraphVisualizer graphVisualizer = new GraphVisualizer(graph);
        String startVertex = graph.vertices().iterator().next();
        Dijkstra dijkstra = new Dijkstra(graph);
        System.out.printf("Start Vertex: %s\n", startVertex);
        for(String vertex : graph.vertices()){
            System.out.printf("Vertex: %s, Distance: %f, Path: %s\n", vertex, dijkstra.distTo(startVertex,vertex), dijkstra.pathTo(startVertex,vertex));
        }

        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}