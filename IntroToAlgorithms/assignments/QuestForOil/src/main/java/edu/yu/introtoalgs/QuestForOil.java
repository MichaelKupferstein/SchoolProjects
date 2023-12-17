package edu.yu.introtoalgs;

public class QuestForOil extends QuestForOilBase {

    private char[][] map;
    private int M;
    private int N;

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
        return dfs(row, column, visited);
    }

    private int dfs(int row, int column, boolean[][] visited) {
        if(row < 0 || row >= N || column < 0 || column >= M) return 0;
        if(map[row][column] == 'U') return 0;
        if(visited[row][column]) return 0;
        visited[row][column] = true;

        int count = 1; // counts current

        count += dfs(row + 1, column, visited);//counts down
        count += dfs(row - 1, column, visited);//counts up
        count += dfs(row, column + 1, visited);//counts right
        count += dfs(row, column - 1, visited);//counts left
        count += dfs(row + 1, column + 1, visited);//counts bottom right corner
        count += dfs(row + 1, column - 1, visited);//counts bottom left corner
        count += dfs(row - 1, column + 1, visited);//counts top right corner
        count += dfs(row - 1, column - 1, visited);//counts top left corner

        return count;

    }
}
