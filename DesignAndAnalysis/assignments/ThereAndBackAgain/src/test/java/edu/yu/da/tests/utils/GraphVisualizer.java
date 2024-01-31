package edu.yu.da.tests.utils;

import edu.yu.da.Edge;
import edu.yu.da.EdgeWeightedGraph;
import edu.yu.da.tests.RandomGraphGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphVisualizer extends JFrame {
    private boolean layoutFinished = false;
    private static final double REPULSION_CONSTANT = 600000.0;
    private static final double ATTRACTION_CONSTANT = 0.01;
    public static final double GRAVITATIONAL_CONSTANT = 0.6;
    private static final double DAMPING = 0.5;
    private static final int ITERATIONS = 1000;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;

    private Map<String, Vertex> vertices = new HashMap<>();

    public GraphVisualizer(EdgeWeightedGraph graph) {
        // Set the frame size
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        // Initialize vertices with random positions
        List<String> vertexList = new ArrayList<>((Collection) graph.vertices());
        Random random = new Random();
        for (int i = 0; i < vertexList.size(); i++) {
            String name = vertexList.get(i);
            int x = random.nextInt(FRAME_WIDTH - 30 );
            int y = random.nextInt(FRAME_HEIGHT - 30);
            vertices.put(name, new Vertex(name.split(" ")[1], x, y));
        }

        // Perform force-directed layout
        for (int i = 0; i < ITERATIONS; i++) {
            // Calculate repulsive forces
            for (Vertex v : vertices.values()) {
                v.setForce(0, 0);
                for (Vertex w : vertices.values()) {
                    if (v != w) {
                        double dx = v.getX() - w.getX();
                        double dy = v.getY() - w.getY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        double force = REPULSION_CONSTANT / (distance * distance);
                        v.addForce(force * dx / distance, force * dy / distance);
                    }
                }
            }

            // Calculate attractive forces
            for (Edge edge : graph.edges()) {
                Vertex v = vertices.get(edge.either());
                Vertex w = vertices.get(edge.other(edge.either()));
                double dx = v.getX() - w.getX();
                double dy = v.getY() - w.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double force = ATTRACTION_CONSTANT * distance;
                v.addForce(-force * dx / distance, -force * dy / distance);
                w.addForce(force * dx / distance, force * dy / distance);
            }
            //Add Gravitiatioanl force
            for (Vertex v : vertices.values()) {
                double dx = (FRAME_WIDTH / 2) - v.getX();
                double dy = (FRAME_HEIGHT / 2) - v.getY();
                v.addForce(dx * GRAVITATIONAL_CONSTANT, dy * GRAVITATIONAL_CONSTANT);
            }

            // Update positions
            for (Vertex v : vertices.values()) {
                int newX = (int) (v.getX() + DAMPING * v.getForceX());
                int newY = (int) (v.getY() + DAMPING * v.getForceY());

                // Add boundary checks
                newX = Math.max(15, Math.min(getWidth() - 15, newX)); // 30 is the diameter of the node
                newY = Math.max(15, Math.min(getHeight() - 15, newY)); // 30 is the diameter of the node

                v.setX(newX);
                v.setY(newY);
            }
        }

        // Create a Swing component to visualize the graph
        GraphPanel graphPanel = new GraphPanel(graph, vertices);

        // Set layout to null and manually set the bounds of the GraphPanel
        setLayout(null);
        graphPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

        // Add the graph panel to the frame
        add(graphPanel);

        // Set the frame size and make it visible
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        layoutFinished = true;
    }

    public static void main(String[] args) {
        EdgeWeightedGraph graph = new RandomGraphGenerator(10).generate();
        new GraphVisualizer(graph);
    }
    public boolean isLayoutFinished() {
        return layoutFinished;
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

        GraphVisualizer parent = (GraphVisualizer) SwingUtilities.getWindowAncestor(this);
        if(parent.isLayoutFinished()){
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
                //DEBUG
                //System.out.println("Draw edge from " + v.getName() + " to " + w.getName() + " with weight " + edge.weight());
                //System.out.println("Drew at coordinates " + midX + ", " + midY);
            }


        }


    }
}

class Vertex {
    private String name;
    private int x;
    private int y;
    private double forceX = 0;
    private double forceY = 0;

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

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setForce(double forceX, double forceY) {
        this.forceX = forceX;
        this.forceY = forceY;
    }

    public void addForce(double forceX, double forceY) {
        this.forceX += forceX;
        this.forceY += forceY;
    }

    public double getForceX() {
        return forceX;
    }

    public double getForceY() {
        return forceY;
    }
}