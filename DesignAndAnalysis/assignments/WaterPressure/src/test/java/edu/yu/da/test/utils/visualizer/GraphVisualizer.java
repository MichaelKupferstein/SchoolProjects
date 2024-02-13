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
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GraphVisualizer{

    Graph<String, DefaultWeightedEdge> graph;
    private final Dimension FULL_SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    private JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter;

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

    public void visualizeGraphWithHierarchicalLayout() {
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();

        // Create a JGraph component for the adapter
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);

        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        displayGraph(graphComponent);
    }

    private JGraphXAdapter<String, DefaultWeightedEdge> createGraphAdapter(){
        graphAdapter  = new JGraphXAdapter<String, DefaultWeightedEdge>(this.graph) {
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

        for(String vertex : this.graph.vertexSet()){
            graphAdapter.setCellStyle("shape=ellipse;perimeter=ellipsePerimeter" , new Object[]{graphAdapter.getVertexToCellMap().get(vertex)});
        }

        return graphAdapter;
    }

    private void displayGraph(mxGraphComponent graphComponent){
        JFrame frame = createFrame(graphComponent);
        frame.setVisible(true);
        keepFrameOpen(frame);
    }

    private JFrame createFrame(mxGraphComponent graphComponent) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(graphComponent, BorderLayout.CENTER);
        frame.getContentPane().add(createControlPanel(), BorderLayout.EAST);
        frame.setSize(FULL_SCREEN.width, FULL_SCREEN.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private JScrollPane createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(controlPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(100, FULL_SCREEN.height));
        addCheckboxesToControlPanel(controlPanel);
        return scrollPane;
    }

    private void addCheckboxesToControlPanel(JPanel controlPanel) {
        HashMap<JCheckBox, String> checkboxToVertexMap = new HashMap<>();
        List<String> sortedVertices = new ArrayList<>(this.graph.vertexSet());
        Collections.sort(sortedVertices);
        for(String vertex : sortedVertices){
            JCheckBox checkbox = new JCheckBox(vertex, true);
            checkbox.addItemListener(e -> toggleVertexVisibility(checkboxToVertexMap, checkbox, e));
            checkboxToVertexMap.put(checkbox, vertex);
            controlPanel.add(checkbox);
        }
    }

    private void toggleVertexVisibility(HashMap<JCheckBox, String> checkboxToVertexMap, JCheckBox checkbox, ItemEvent e) {
        String vertexToToggle = checkboxToVertexMap.get(checkbox);
        Object cell = graphAdapter.getVertexToCellMap().get(vertexToToggle);
        if (e.getStateChange() == ItemEvent.SELECTED) {
            graphAdapter.getModel().setVisible(cell, true);
        } else {
            graphAdapter.getModel().setVisible(cell, false);
        }
    }

    private void keepFrameOpen(JFrame frame) {
        while (frame.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}