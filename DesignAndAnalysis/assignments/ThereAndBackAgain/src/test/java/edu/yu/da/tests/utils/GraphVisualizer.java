package edu.yu.da.tests.utils;

import edu.yu.da.Edge;
import edu.yu.da.EdgeWeightedGraph;
import edu.yu.da.tests.RandomGraphGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphVisualizer extends JFrame {
    private Map<String, Vertex> vertices = new HashMap<>();

    public GraphVisualizer(EdgeWeightedGraph graph) {
        // Create vertices with grid layout positions
        List<String> vertexList = new ArrayList<>((Collection) graph.vertices());
        int gridSize = (int) Math.ceil(Math.sqrt(vertexList.size()));
        int cellSize = 800 / gridSize;
        for (int i = 0; i < vertexList.size(); i++) {
            String name = vertexList.get(i);
            int x = (i % gridSize) * cellSize + cellSize / 2;
            int y = (i / gridSize) * cellSize + cellSize / 2;
            vertices.put(name, new Vertex(name.split(" ")[1], x, y));
        }

        // Create a Swing component to visualize the graph
        GraphPanel graphPanel = new GraphPanel(graph, vertices);

        // Add the graph panel to the frame
        add(graphPanel);

        // Set the frame size and make it visible
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        EdgeWeightedGraph graph = new RandomGraphGenerator(10).generate();
        new GraphVisualizer(graph);
    }
}

class GraphPanel extends JPanel {
    private EdgeWeightedGraph graph;
    private Map<String, Vertex> vertices;

    public GraphPanel(EdgeWeightedGraph graph, Map<String, Vertex> vertices) {
        this.graph = graph;
        this.vertices = vertices;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the nodes
        for (Vertex vertex : vertices.values()) {
            g.drawOval(vertex.getX() - 15, vertex.getY() - 15, 30, 30); // Thicker border
            g.setColor(Color.BLACK);
            g.drawString(vertex.getName(), vertex.getX() - 5 , vertex.getY() + 5); // Draw names underneath nodes
        }

        // Draw the edges
        for (Edge edge : graph.edges()) {
            Vertex v = vertices.get(edge.either());
            Vertex w = vertices.get(edge.other(edge.either()));
            g.drawLine(v.getX(), v.getY(), w.getX(), w.getY());

            // Draw the weight of the edge
            int midX = (v.getX() + w.getX()) / 2;
            int midY = (v.getY() + w.getY()) / 2;
            g.drawString(String.format("%.2f", edge.weight()), midX, midY);
        }
    }
}

class Vertex {
    private String name;
    private int x;
    private int y;

    public Vertex(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}