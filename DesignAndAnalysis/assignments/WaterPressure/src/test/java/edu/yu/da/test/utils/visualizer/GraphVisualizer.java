package edu.yu.da.test.utils.visualizer;
//
//import com.mxgraph.layout.*;
//import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
//import com.mxgraph.swing.mxGraphComponent;
//import edu.yu.da.graph.DirectedEdge;
//import edu.yu.da.graph.EdgeWeightedDirectedGraph;
//import org.jgrapht.Graph;
//import org.jgrapht.GraphPath;
//import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
//import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
//import org.jgrapht.ext.JGraphXAdapter;
//import org.jgrapht.graph.DefaultWeightedEdge;
//import org.jgrapht.graph.DirectedWeightedMultigraph;
//
//import javax.swing.*;
//import javax.swing.Timer;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import java.awt.*;
//import java.awt.event.ItemEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseMotionListener;
//import java.util.*;
//import java.util.List;
//
///**
// * This class is used to visualize a graph using different layouts.
// * It provides a GUI with a control panel for selecting nodes and a separate panel for selecting graph algorithms.
// */
public class GraphVisualizer{}
//
//    // The graph to visualize
//    private Graph<String, DefaultWeightedEdge> graph;
//    // The full screen dimension
//    private final Dimension FULL_SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
//    // The adapter to convert the graph into a JGraph
//    private JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter;
//    // A map to associate checkboxes with vertices
//    private HashMap<JCheckBox, String> checkboxToVertexMap = new HashMap<>();
//    private JComboBox<String> sourceVertexComboBox;
//    private JComboBox<String> targetVertexComboBox;
//
//    /**
//     * Constructs a GraphVisualizer with the given graph.
//     * @param graph The graph to visualize
//     */
//    public GraphVisualizer(EdgeWeightedDirectedGraph graph) {
//        this.graph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
//        for(String vertex : graph.vertices()) {
//            this.graph.addVertex(vertex);
//        }
//        for(DirectedEdge edge : graph.edges()) {
//            this.graph.addEdge(edge.from(), edge.to());
//            this.graph.setEdgeWeight(this.graph.getEdge(edge.from(), edge.to()), edge.weight());
//        }
//    }
//
//    /**
//     * Visualizes the graph with a circle layout.
//     */
//    public void visualizeGraphWithCircleLayout() {
//        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();
//
//        // Create a JGraph component for the adapter
//        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
//
//        // Create a layout for the graph
//        mxCircleLayout layout = new mxCircleLayout(graphAdapter);
//        layout.setMoveCircle(true);
//
//        layout.execute(graphAdapter.getDefaultParent());
//
//        displayGraph(graphComponent);
//    }
//
//    /**
//     * Visualizes the graph with a fast organic layout.
//     */
//    public void visualizeGraphWithFastOrganicLayout() {
//        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();
//
//        // Create a JGraph component for the adapter
//        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
//
//        mxFastOrganicLayout layout = new mxFastOrganicLayout(graphAdapter);
//        layout.execute(graphAdapter.getDefaultParent());
//
//        displayGraph(graphComponent);
//    }
//
//    /**
//     * Visualizes the graph with a hierarchical layout.
//     */
//    public void visualizeGraphWithHierarchicalLayout() {
//        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = createGraphAdapter();
//
//        // Create a JGraph component for the adapter
//        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
//
//        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
//        layout.execute(graphAdapter.getDefaultParent());
//
//        displayGraph(graphComponent);
//    }
//
//    /**
//     * Creates a JGraphXAdapter for the graph and configures it.
//     * @return The created and configured JGraphXAdapter
//     */
//    private JGraphXAdapter<String, DefaultWeightedEdge> createGraphAdapter(){
//        graphAdapter  = new JGraphXAdapter<String, DefaultWeightedEdge>(this.graph) {
//            @Override
//            public String convertValueToString(Object cell) {
//                if (model.isEdge(cell)) {
//                    Object value = model.getValue(cell);
//                    if (value instanceof DefaultWeightedEdge) {
//                        DefaultWeightedEdge edge = (DefaultWeightedEdge) value;
//                        return String.valueOf(graph.getEdgeWeight(edge));
//                    }
//                }
//                return super.convertValueToString(cell);
//            }
//            @Override
//            public boolean isCellEditable(Object cell) {
//                return false;
//            }
//            @Override
//            public boolean isCellConnectable(Object cell) {
//                return false;
//            }
//            @Override
//            public boolean isLabelMovable(Object cell) {
//                return false;
//            }
//            @Override
//            public boolean isCellMovable(Object cell) {
//                return !model.isEdge(cell);
//            }
//        };
//
//        for(String vertex : this.graph.vertexSet()){
//            graphAdapter.setCellStyle("shape=ellipse;perimeter=ellipsePerimeter" , new Object[]{graphAdapter.getVertexToCellMap().get(vertex)});
//        }
//
//        return graphAdapter;
//    }
//
//    /**
//     * Displays the graph in a JFrame.
//     * @param graphComponent The JGraph component to display
//     */
//    private void displayGraph(mxGraphComponent graphComponent){
//        JFrame frame = createFrame(graphComponent);
//        frame.setVisible(true);
//        graphComponent.setWheelScrollingEnabled(true);
//        graphComponent.getVerticalScrollBar().setUnitIncrement(10);
//        graphComponent.getHorizontalScrollBar().setUnitIncrement(10);
//
//        graphComponent.setPanning(true);
//        addMouseMotionListener(graphComponent);
//
//        keepFrameOpen(frame);
//    }
//    private void addMouseMotionListener(mxGraphComponent graphComponent) {
//        graphComponent.getGraphControl().addMouseMotionListener(new MouseMotionListener() {
//            Point lastPoint = null;
//
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                if (lastPoint != null) {
//                    int dx = e.getX() - lastPoint.x;
//                    int dy = e.getY() - lastPoint.y;
//
//                    Rectangle view = graphComponent.getViewport().getViewRect();
//                    view.x -= dx;
//                    view.y -= dy;
//
//                    graphComponent.getGraphControl().scrollRectToVisible(view);
//                }
//
//                lastPoint = e.getPoint();
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                lastPoint = null;
//            }
//        });
//    }
//
//    /**
//     * Creates a JFrame to display the graph.
//     * @param graphComponent The JGraph component to display
//     * @return The created JFrame
//     */
//    private JFrame createFrame(mxGraphComponent graphComponent) {
//        JFrame frame = new JFrame();
//        frame.getContentPane().add(graphComponent, BorderLayout.CENTER);
//
//        // Create a tabbed pane
//        JTabbedPane tabbedPane = new JTabbedPane();
//
//        // Add the control panel and the algorithm dropdown panel to the tabbed pane
//        tabbedPane.addTab("Control Panel", createControlPanel());
//        tabbedPane.addTab("Algorithm Selection", createAlgorithmPanel());
//
//        frame.getContentPane().add(tabbedPane, BorderLayout.EAST);
//
//        frame.setSize(FULL_SCREEN.width, FULL_SCREEN.height);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        return frame;
//    }
//
//    /**
//     * Creates a JPanel for the algorithm buttons.
//     * @return The created JPanel
//     */
//    private JPanel createAlgorithmPanel() {
//        JPanel algorithmPanel = new JPanel();
//        algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));
//        addAlgorithmButtons(algorithmPanel);
//        addSourceAndTargetVertexComboBoxes(algorithmPanel);
//        addClearPathButton(algorithmPanel);
//
//        return algorithmPanel;
//    }
//
//    private void addClearPathButton(JPanel algorithmPanel){
//        // Create "Clear Path" button
//        JButton clearPathButton = new JButton("Clear Path");
//        clearPathButton.addActionListener(e -> clearPath());
//        algorithmPanel.add(clearPathButton);
//    }
//
//    /**
//     * Creates a JScrollPane for the control panel.
//     * @return The created JScrollPane
//     */
//    private JScrollPane createControlPanel() {
//        JPanel controlPanel = new JPanel();
//        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
//        JScrollPane scrollPane = new JScrollPane(controlPanel);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setPreferredSize(new Dimension(100, FULL_SCREEN.height));
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//
//        addSelectButtons(controlPanel);
//        addSearchField(controlPanel);
//        addCheckboxesToControlPanel(controlPanel);
//
//        return scrollPane;
//    }
//
//    /**
//     * Adds the "Select All" and "Unselect All" buttons to the control panel.
//     * @param controlPanel The control panel to add the buttons to
//     */
//    private void addSelectButtons(JPanel controlPanel) {
//        // Create "Select All" button
//        JButton selectAllButton = new JButton("Select All");
//        selectAllButton.addActionListener(e -> selectAllNodes());
//        controlPanel.add(selectAllButton);
//
//        // Create "Unselect All" button
//        JButton unselectAllButton = new JButton("Unselect All");
//        unselectAllButton.addActionListener(e -> unselectAllNodes());
//        controlPanel.add(unselectAllButton);
//    }
//
//    /**
//     * Adds a search field to the control panel.
//     * @param controlPanel The control panel to add the search field to
//     */
//    private void addSearchField(JPanel controlPanel) {
//        // Create "Search" field
//        JTextField searchField = new JTextField();
//        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height)); // Set maximum size
//        searchField.getDocument().addDocumentListener(new DocumentListener() {
//            public void changedUpdate(DocumentEvent e) {
//                searchNode(searchField.getText());
//            }
//            public void removeUpdate(DocumentEvent e) {
//                searchNode(searchField.getText());
//            }
//            public void insertUpdate(DocumentEvent e) {
//                searchNode(searchField.getText());
//            }
//        });
//        controlPanel.add(searchField);
//    }
//
//    /**
//     * Adds the algorithm buttons to the algorithm panel.
//     * @param controlPanel The algorithm panel to add the buttons to
//     */
//    private void addAlgorithmButtons(JPanel controlPanel) {
//        // Create a separate panel for the buttons
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
//
//        // Create "Shortest Path" button
//        JButton shortestPathButton = new JButton("Shortest Path");
//        shortestPathButton.addActionListener(e -> visualizeShortestPath());
//        buttonPanel.add(shortestPathButton);
//
//        // Create "Minimum Spanning Tree" button
//        JButton minimumSpanningTreeButton = new JButton("Minimum Spanning Tree");
//        minimumSpanningTreeButton.addActionListener(e -> visualizeMinimumSpanningTree());
//        buttonPanel.add(minimumSpanningTreeButton);
//
//        // Create "Other Algorithm" button
//        JButton otherAlgorithmButton = new JButton("Other Algorithm");
//        otherAlgorithmButton.addActionListener(e -> visualizeOtherAlgorithm());
//        buttonPanel.add(otherAlgorithmButton);
//
//
//
//        // Add the button panel to the control panel
//        controlPanel.add(buttonPanel);
//    }
//
//    private void clearPath() {
//        for (DefaultWeightedEdge edge : graph.edgeSet()) {
//            graphAdapter.setCellStyle("strokeColor=#6482B9", new Object[]{graphAdapter.getEdgeToCellMap().get(edge)});
//        }
//    }
//
//    /**
//     * Selects all nodes in the graph.
//     */
//    private void selectAllNodes() {
//        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
//            checkbox.setSelected(true);
//        }
//    }
//
//    /**
//     * Unselects all nodes in the graph.
//     */
//    private void unselectAllNodes() {
//        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
//            checkbox.setSelected(false);
//        }
//    }
//
//    /**
//     * Searches for a node in the graph.
//     * @param nodeName The name of the node to search for
//     */
//    private void searchNode(String nodeName) {
//        for (JCheckBox checkbox : checkboxToVertexMap.keySet()) {
//            if (checkboxToVertexMap.get(checkbox).contains(nodeName)) {
//                checkbox.setVisible(true); // Show the checkbox if it matches the search query
//            } else {
//                checkbox.setVisible(false); // Hide the checkbox if it doesn't match the search query
//            }
//        }
//    }
//
//    /**
//     * Adds checkboxes for each vertex to the control panel.
//     * @param controlPanel The control panel to add the checkboxes to
//     */
//    private void addCheckboxesToControlPanel(JPanel controlPanel) {
//        List<String> sortedVertices = new ArrayList<>(this.graph.vertexSet());
//
//        // Use a custom comparator that compares the numeric values of the vertices
//        Collections.sort(sortedVertices, (v1, v2) -> {
//            int num1 = Integer.parseInt(v1.substring(5)); // Assuming the vertex name is "Node X"
//            int num2 = Integer.parseInt(v2.substring(5));
//            return Integer.compare(num1, num2);
//        });
//
//        for(String vertex : sortedVertices){
//            JCheckBox checkbox = new JCheckBox(vertex, true);
//            checkbox.addItemListener(e -> toggleVertexVisibility(checkboxToVertexMap, checkbox, e));
//            checkboxToVertexMap.put(checkbox, vertex);
//            controlPanel.add(checkbox);
//        }
//    }
//
//    /**
//     * Toggles the visibility of a vertex in the graph.
//     * @param checkboxToVertexMap The map associating checkboxes with vertices
//     * @param checkbox The checkbox associated with the vertex
//     * @param e The item event
//     */
//    private void toggleVertexVisibility(HashMap<JCheckBox, String> checkboxToVertexMap, JCheckBox checkbox, ItemEvent e) {
//        String vertexToToggle = checkboxToVertexMap.get(checkbox);
//        Object cell = graphAdapter.getVertexToCellMap().get(vertexToToggle);
//        if (e.getStateChange() == ItemEvent.SELECTED) {
//            graphAdapter.getModel().setVisible(cell, true);
//        } else {
//            graphAdapter.getModel().setVisible(cell, false);
//        }
//    }
//
//    /**
//     * Keeps the JFrame open.
//     * @param frame The JFrame to keep open
//     */
//    private void keepFrameOpen(JFrame frame) {
//        while (frame.isVisible()) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void addSourceAndTargetVertexComboBoxes(JPanel controlPanel){
//        //Create source vertext selection
//        controlPanel.add(new JLabel("Source Vertex:"));
//        List<String> sortedVertices = new ArrayList<>(this.graph.vertexSet());
//        Collections.sort(sortedVertices, (v1, v2) -> {
//            int num1 = Integer.parseInt(v1.substring(5)); // Assuming the vertex name is "Node X"
//            int num2 = Integer.parseInt(v2.substring(5));
//            return Integer.compare(num1, num2);
//        });
//        sourceVertexComboBox = new JComboBox<>(sortedVertices.toArray(new String[0]));
//        sourceVertexComboBox.setMaximumSize(new Dimension(200,30));
//        controlPanel.add(sourceVertexComboBox);
//
//        //Create target vertext selection
//        controlPanel.add(new JLabel("Target Vertex:"));
//        targetVertexComboBox = new JComboBox<>(sortedVertices.toArray(new String[0]));
//        targetVertexComboBox.setMaximumSize(new Dimension(200,30));
//        controlPanel.add(targetVertexComboBox);
//    }
//
//    /**
//     * Visualizes the shortest path algorithm.
//     * This is a placeholder method to be implemented.
//     */
//    private void visualizeShortestPath() {
//        String source = (String) sourceVertexComboBox.getSelectedItem();
//        String target = (String) targetVertexComboBox.getSelectedItem();
//        List<String> shortestPath = shortestPath(source, target);
//
//        if(shortestPath == null){
//            JOptionPane.showMessageDialog(null, "There is no path from " + source + " to " + target, "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        for(int i = 0; i < shortestPath.size() - 1; i++){
//            String vertex1 = shortestPath.get(i);
//            String vertex2 = shortestPath.get(i + 1);
//            DefaultWeightedEdge edge = graph.getEdge(vertex1, vertex2);
//            graphAdapter.setCellStyle("strokeColor=red", new Object[]{graphAdapter.getEdgeToCellMap().get(edge)});
//        }
//    }
//
//    private List<String> shortestPath(String source, String target){
//        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
//        GraphPath<String,DefaultWeightedEdge> path = dijkstraAlg.getPath(source, target);
//        return path != null ? path.getVertexList() : null;
//    }
//
//    /**
//     * Visualizes the minimum spanning tree algorithm.
//     * This is a placeholder method to be implemented.
//     */
//    private void visualizeMinimumSpanningTree() {
//        //Create an instance of the PrimMinimumSpanningTree algorithm
//        PrimMinimumSpanningTree<String, DefaultWeightedEdge> primAlg = new PrimMinimumSpanningTree<>(graph);
//        //Get the edges of the minimum spanning tree
//        Set<DefaultWeightedEdge> spanningTreeEdges = primAlg.getSpanningTree().getEdges();
//
//        //Iterate over all the edges in the graoh
//        for(DefaultWeightedEdge edge : graph.edgeSet()){
//            //If the edge is part of the minimum spanning tree, change its color to red
//            if(spanningTreeEdges.contains(edge)){
//                graphAdapter.setCellStyle("strokeColor=red", new Object[]{graphAdapter.getEdgeToCellMap().get(edge)});
//            }else {
//                //Otherwise, change its color to the default color
//                graphAdapter.setCellStyle("strokeColor=#6482B9", new Object[]{graphAdapter.getEdgeToCellMap().get(edge)});
//            }
//
//        }
//
//    }
//
//    /**
//     * Visualizes another algorithm.
//     * This is a placeholder method to be implemented.
//     */
//    private void visualizeOtherAlgorithm() {
//        // Implement the visualization for the other algorithm
//    }
//}