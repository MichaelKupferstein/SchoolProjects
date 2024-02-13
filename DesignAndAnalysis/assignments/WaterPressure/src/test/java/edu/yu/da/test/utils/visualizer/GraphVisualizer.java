package edu.yu.da.test.utils.visualizer;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import edu.yu.da.graph.DirectedEdge;
import edu.yu.da.graph.EdgeWeightedDirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.swing.*;
import java.awt.*;

/**
 * A class to visualize graphs using JGraphX library.
 */
public class GraphVisualizer{

    Graph<String, DefaultWeightedEdge> graph;
    private final Dimension FULL_SCREEN = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Constructor for GraphVisualizer.
     * @param graph The graph to be visualized.
     */
    public GraphVisualizer(EdgeWeightedDirectedGraph graph) {
        this.graph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
        for(String vertex : graph.vertices()) {
            this.graph.addVertex(vertex);
        }
        for(DirectedEdge edge : graph.edges()) {
            this.graph.addEdge(edge.from(), edge.to());
            this.graph.setEdgeWeight(this.graph.getEdge(edge.from(), edge.to()), edge.weight());
        }
    }

    /**
     * Visualizes the graph with a circle layout.
     */
    public void visualizeGraphWithCircleLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        // Create a layout for the graph
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
        layout.setMoveCircle(true);

        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);
    }

    /**
     * Visualizes the graph with a fast organic layout.
     */
    public void visualizeGraphWithFastOrganicLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);
    }

    /**
     * Visualizes the graph with a hierarchical layout.
     */
    public void visualizeGraphWithHierarchicalLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);
    }

    /**
     * Creates a JGraphXAdapter for the graph.
     * @return The created JGraphXAdapter.
     */
    private JGraphXAdapter<String, DefaultWeightedEdge> createGraphAdapter(){
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter  = new JGraphXAdapter<String, DefaultWeightedEdge>(this.graph) {
            @Override
            public String convertValueToString(Object cell) {
                if (model.isEdge(cell)) {
                    Object value = model.getValue(cell);
                    if (value instanceof DefaultWeightedEdge) {
                        DefaultWeightedEdge edge = (DefaultWeightedEdge) value;
                        return String.valueOf(graph.getEdgeWeight(edge));
                    }
                }
                return super.convertValueToString(cell);
            }
            @Override
            public boolean isCellEditable(Object cell) {
                return false;
            }
            @Override
            public boolean isCellConnectable(Object cell) {
                return false;
            }
            @Override
            public boolean isLabelMovable(Object cell) {
                return false;
            }
            @Override
            public boolean isCellMovable(Object cell) {
                return !model.isEdge(cell);
            }
        };

        // Set the shape of the vertices
        for(String vertex : this.graph.vertexSet()){
            graphAdapter.setCellStyle("shape=ellipse;perimeter=ellipsePerimeter" , new Object[]{graphAdapter.getVertexToCellMap().get(vertex)});
        }

        return graphAdapter;
    }

    /**
     * Displays the graph in a JFrame.
     * @param graphComponent The graph component to be displayed.
     */
    private void displayGraph(mxGraphComponent graphComponent){
        JFrame frame = new JFrame();
        frame.getContentPane().add(graphComponent);
        frame.setSize(FULL_SCREEN.width, FULL_SCREEN.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Wait for the user to close the window
        while (frame.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
