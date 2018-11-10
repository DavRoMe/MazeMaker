
package mazemaker;

public class MazeCell {

    private int x;
    private int y;
    
    private int index_i;
    private int index_j;
    
    private boolean wall_up;
    private boolean wall_down;
    private boolean wall_left;
    private boolean wall_right;
    
    private boolean path;
    
    public enum state { UNVISITED, VISITED, REVISITED, CURRENT }
    private state st;

    public MazeCell() {
        this.st = state.UNVISITED;
        wall_down = true;
        wall_left = true;
        wall_right = true;
        wall_up = true;
        path = false;
    }

    public MazeCell(int x, int y) {
        this.x = x;
        this.y = y;
        this.st = state.UNVISITED;
        wall_down = true;
        wall_left = true;
        wall_right = true;
        wall_up = true;
        path = false;
    }

    ////////////////////////////////////////////////////////////////
    
    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }
    
    public void setState(state st) { this.st = st; }
    
    public void setIndex(int i, int j) {
        this.index_i = i;
        this.index_j = j;
    }
    
    public void setWallUp(boolean wall) { wall_up = wall; }
    
    public void setWallDown(boolean wall) { wall_down = wall; }
    
    public void setWallLeft(boolean wall) { wall_left = wall; }
    
    public void setWallRight(boolean wall) { wall_right = wall; }
    
    public void makePath() { path = true; }
    
    public state getState() { return st; }
    
    /////////////////////////////////////////////////////////////////
    
    public boolean getWallUp() { return wall_up; }
    
    public boolean getWallDown() { return wall_down; }
    
    public boolean getWallLeft() { return wall_left; }
    
    public boolean getWallRight() { return wall_right; }

    public int getIndexI() { return index_i; }
    
    public int getIndexJ() { return index_j; }
    
    public int getXpos() { return x; }

    public int getYpos() { return y; }
    
    public boolean isPath() { return path; }
}

