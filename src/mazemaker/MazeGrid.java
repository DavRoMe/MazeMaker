
package mazemaker;

public class MazeGrid {

    public final int RECT_W;
    public final int RECT_H;
    public final int WALL_THICKNESS = 2;
    private final int ROWS;
    private final int COLUMNS;
    private final MazeCell[][] cells;
    private int visited_cells;
    
    public MazeGrid(int width, int height, int columns, int rows) {
        visited_cells = 0;
        ROWS = rows; COLUMNS = columns;
        RECT_W = width / columns;
        RECT_H = height / rows;
        
        cells = new MazeCell[COLUMNS][ROWS];
        
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                cells[i][j] = new MazeCell(i * RECT_W, j * RECT_H);
            }
        }
    }
    
    public void addVisited() { visited_cells++; }
    
    public int cellsVisited() { return visited_cells; }

    public MazeCell getCell(int i, int j) { return cells[i][j]; }
    
    public MazeCell cellDown(MazeCell cell) {
        return cells[cell.getIndexI()][cell.getIndexJ() + 1];
    }

    public MazeCell cellLeft(MazeCell cell) {
        return cells[cell.getIndexI() - 1][cell.getIndexJ()];
    }

    public MazeCell cellRight(MazeCell cell) {
        return cells[cell.getIndexI() + 1][cell.getIndexJ()];
    }
    
    public int getRows() { return ROWS; }
    
    public int getColumns() { return COLUMNS; } 
    
    public int gridSize() { return COLUMNS * ROWS; }
        
    public MazeCell cellUp(MazeCell cell) {
        return cells[cell.getIndexI()][cell.getIndexJ() - 1];
    }

    public boolean validRight(MazeCell cell) {
        return (cell.getIndexI() < COLUMNS - 1);
    }

    public boolean validLeft(MazeCell cell) {
        return (cell.getIndexI() > 0);
    }

    public boolean validDown(MazeCell cell) {
        return (cell.getIndexJ() < ROWS - 1);        
    }

    public boolean validUp(MazeCell cell) {
        return (cell.getIndexJ() > 0);
    }
    
    public boolean hasNeighbours(MazeCell cell) {
        if (validLeft(cell)) {
            if (getCell(cell.getIndexI() - 1, cell.getIndexJ()).getState() == MazeCell.state.UNVISITED) {
                return true;
            }
        }
        if (validRight(cell)) {
            if (getCell(cell.getIndexI() + 1, cell.getIndexJ()).getState() == MazeCell.state.UNVISITED) {
                return true;
            }
        }
        if (validUp(cell)) {
            if (getCell(cell.getIndexI(), cell.getIndexJ() - 1).getState() == MazeCell.state.UNVISITED) {
                return true;
            }
        }
        if (validDown(cell)) {
            if (getCell(cell.getIndexI(), cell.getIndexJ() + 1).getState() == MazeCell.state.UNVISITED) {
                return true;
            }
        }
        return false;
    }
}

