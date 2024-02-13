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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    HashMap<JCheckBox, String> checkboxToVertexMap = new HashMap<>();

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

        // Create a tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add the control panel and the algorithm dropdown panel to the tabbed pane
        tabbedPane.addTab("Control Panel", createControlPanel());
        tabbedPane.addTab("Algorithm Selection", createAlgorithmPanel());

        frame.getContentPane().add(tabbedPane, BorderLayout.EAST);

        frame.setSize(FULL_SCREEN.width, FULL_SCREEN.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private JPanel createAlgorithmPanel() {
        JPanel algorithmPanel = new JPanel();
        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
        addAlgorithmButtons(algorithmPanel);
        return algorithmPanel;
    }

    private JScrollPane createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(controlPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(100, FULL_SCREEN.height));

        addSelectButtons(controlPanel);
        addSearchField(controlPanel);
        addCheckboxesToControlPanel(controlPanel);

        return scrollPane;
    }

    private void addSelectButtons(JPanel controlPanel) {
        // Create "Select All" button
        JButton selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(e -> selectAllNodes());
        controlPanel.add(selectAllButton);

        // Create "Unselect All" button
        JButton unselectAllButton = new JButton("Unselect All");
        unselectAllButton.addActionListener(e -> unselectAllNodes());
        controlPanel.add(unselectAllButton);
    }

    private void addSearchField(JPanel controlPanel) {
        // Create "Search" field
        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height)); // Set maximum size
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                searchNode(searchField.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                searchNode(searchField.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                searchNode(searchField.getText());
            }
        });
        controlPanel.add(searchField);
    }

    private void addAlgorithmButtons(JPanel controlPanel) {
        // Create a separate panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Create "Shortest Path" button
        JButton shortestPathButton = new JButton("Shortest Path");
        shortestPathButton.addActionListener(e -> visualizeShortestPath());
        buttonPanel.add(shortestPathButton);

        // Create "Minimum Spanning Tree" button
        JButton minimumSpanningTreeButton = new JButton("Minimum Spanning Tree");
        minimumSpanningTreeButton.addActionListener(e -> visualizeMinimumSpanningTree());
        buttonPanel.add(minimumSpanningTreeButton);

        // Create "Other Algorithm" button
        JButton otherAlgorithmButton = new JButton("Other Algorithm");
        otherAlgorithmButton.addActionListener(e -> visualizeOtherAlgorithm());
        buttonPanel.add(otherAlgorithmButton);

        // Add the button panel to the control panel
        controlPanel.add(buttonPanel);
    }

    private void selectAllNodes() {
        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
            checkbox.setSelected(true);
        }
    }

    private void unselectAllNodes() {
        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
            checkbox.setSelected(false);
        }
    }

    private void searchNode(String nodeName) {
        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
            if (checkboxToVertexMap.get(checkbox).contains(nodeName)) {
                checkbox.setVisible(true); // Show the checkbox if it matches the search query
            } else {
                checkbox.setVisible(false); // Hide the checkbox if it doesn't match the search query
            }
        }
    }

    private void addCheckboxesToControlPanel(JPanel controlPanel) {
        List<String> sortedVertices = new ArrayList<>(this.graph.vertexSet());

        // Use a custom comparator that compares the numeric values of the vertices
        Collections.sort(sortedVertices, (v1, v2) -> {
            int num1 = Integer.parseInt(v1.substring(5)); // Assuming the vertex name is "Node X"
            int num2 = Integer.parseInt(v2.substring(5));
            return Integer.compare(num1, num2);
        });

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

    // Placeholder methods for the graph algorithms
    private void visualizeShortestPath() {
        // Implement the visualization for the shortest path algorithm
    }

    private void visualizeMinimumSpanningTree() {
        // Implement the visualization for the minimum spanning tree algorithm
    }

    private void visualizeOtherAlgorithm() {
        // Implement the visualization for the other algorithm
    }
}