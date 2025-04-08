package edu.yu.da.nf;

/**
 * @author William Fiset, william.alexandre.fiset@gmail.com
 * Code from: https://github.com/williamfiset/Algorithms/blob/master/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/EdmondsKarpAdjacencyList.java
 * */
import static java.lang.Math.min;

import java.util.*;

public class EdmondsKarpAdjacencyList extends NetworkFlowSolverBase {

    /**
     * Creates an instance of a flow network solver. Use the {@link #addEdge(int, int, int)} method to
     * add edges to the graph.
     *
     * @param n - The number of nodes in the graph including source and sink nodes.
     * @param s - The index of the source node, 0 <= s < n
     * @param t - The index of the sink node, 0 <= t < n, t != s
     */
    public EdmondsKarpAdjacencyList(int n, int s, int t) {
        super(n, s, t);
    }

    // Run Edmonds-Karp and compute the max flow from the source to the sink node.
    @Override
    public void solve() {
        long flow;
        do {
            markAllNodesAsUnvisited();
            flow = bfs();
            maxFlow += flow;
        } while (flow != 0);

        for (int i = 0; i < n; i++) if (visited(i)) minCut[i] = true;
    }

    private long bfs() {
        Edge[] prev = new Edge[n];

        // The queue can be optimized to use a faster queue
        Queue<Integer> q = new ArrayDeque<>(n);
        visit(s);
        q.offer(s);

        // Perform BFS from source to sink
        while (!q.isEmpty()) {
            int node = q.poll();
            if (node == t) break;

            for (Edge edge : graph[node]) {
                long cap = edge.remainingCapacity();
                if (cap > 0 && !visited(edge.to)) {
                    visit(edge.to);
                    prev[edge.to] = edge;
                    q.offer(edge.to);
                }
            }
        }

        // Sink not reachable!
        if (prev[t] == null) return 0;

        long bottleNeck = Long.MAX_VALUE;

        // Find augmented path and bottle neck
        for (Edge edge = prev[t]; edge != null; edge = prev[edge.from])
            bottleNeck = min(bottleNeck, edge.remainingCapacity());

        // Retrace augmented path and update flow values.
        for (Edge edge = prev[t]; edge != null; edge = prev[edge.from]) edge.augment(bottleNeck);

        // Return bottleneck flow
        return bottleNeck;
    }
}
