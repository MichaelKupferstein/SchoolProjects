package edu.yu.da.test.utils.visualizer;


import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxEllipseShape;
import com.mxgraph.shape.mxIShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxCellState;
import edu.yu.da.DirectedEdge;
import edu.yu.da.EdgeWeightedDirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GraphVisualizer{

    Graph<String, DefaultWeightedEdge> graph;
    private final Dimension FULL_SCREEN = Toolkit.getDefaultToolkit().getScreenSize();

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

    public void visualizeGraphWithCircleLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        // Create a layout for the graph
        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
//        double radius = layout.getRadius();
//        layout.setX0((FULL_SCREEN.width / 2) - radius);
//        layout.setY0((FULL_SCREEN.height / 2) - radius);
        layout.setMoveCircle(true);

        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);
    }
    public void visualizeGraphWithFastOrganicLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        mxFastOrganicLayout layout = new mxFastOrganicLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);


    }

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
