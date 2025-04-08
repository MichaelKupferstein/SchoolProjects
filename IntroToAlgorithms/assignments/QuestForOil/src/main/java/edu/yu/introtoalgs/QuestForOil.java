package edu.yu.introtoalgs;

import java.util.LinkedList;
import java.util.Queue;

public class QuestForOil extends QuestForOilBase {

    private char[][] map;
    private int M;
    private int N;

    private final int[] up = {-1, 0};
    private final int[] down = {1, 0};
    private final int[] right = {0, 1};
    private final int[] left = {0, -1};
    private final int[] upRight = {-1, 1};
    private final int[] upLeft = {-1, -1};
    private final int[] downRight = {1, 1};
    private final int[] downLeft = {1, -1};

    /**
     * Constructor supplies the map.
     *
     * @param map a non-null, N by M (not necessarily a square!), two-dimensional
     *            matrix in which each element is either an 'S' (safe) or a 'U' (unsafe) to
     *            walk on. It's the client's responsibility to ensure that the matrix isn't
     *            "jagged". The client relinquishes ownership to the implementation.
     */
    public QuestForOil(char[][] map) {
        super(map);
        if(map == null) throw new IllegalArgumentException("Map cannot be null");
        this.map = map;
        this.N = map.length;
        this.M = map[0].length;
    }

    /**
     * Specifies the initial "start the search" square, explore the map to find
     * the maximum number of squares contiguous to that square (including the
     * "start the search" square itself).
     * <p>
     * Note: the client is allowed to repeatedly invoke this method, e.g., with
     * different start search squares, on the same QuestForOil instance.
     *
     * @param row    the row of the initial "start the search" square, 0..N-1
     *               indexing.
     * @param column the column of the initial "start the search" square, 0..M-1
     *               indexing.
     * @return the maximum number of squares contiguous to the inital square.
     */
    @Override
    public int nContiguous(int row, int column) {
        if(row < 0 || row >= N || column < 0 || column >= M) throw new IllegalArgumentException("Row and column must be within the bounds of the map");
        if(map[row][column] == 'U') return 0;
        boolean[][] visited = new boolean[N][M];
        return bfs(row, column, visited);
    }

    private int bfs(int row, int column, boolean[][] visited) {
        Queue<int[]> queue = new LinkedList<>();
        int[] start = {row, column};
        queue.offer(start);
        visited[row][column] = true;
        int count = 0;

        while (!queue.isEmpty()) {
            int[] square = queue.poll();
            row = square[0];
            column = square[1];
            count++;

            int[][] directions = {up, down, right, left, upRight, upLeft, downRight, downLeft};
            for (int[] direction : directions) {
                int newRow = row + direction[0];
                int newColumn = column + direction[1];

                if(newRow >= 0 && newRow < N && newColumn >= 0 && newColumn < M && !visited[newRow][newColumn] && map[newRow][newColumn] == 'S') {
                    queue.offer(new int[]{newRow, newColumn});
                    visited[newRow][newColumn] = true;
                }
            }
        }

        return count;
    }
}
