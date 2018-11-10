package mazemaker;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class FrameMaze extends JDialog {
    
    PanelMaze panel;
    
    public FrameMaze(int width, int height, int columns, int rows, int delay, long seed, boolean showPath) {
        super(new Frame(), true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        pack();

        panel = new PanelMaze(width, height, columns, rows, delay, seed, showPath);
        add(panel);
        setTitle("Laberinto");
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panel.dispose();
            }
        });
    }
    
    public void draw() {panel.drawMaze();}
    
}

class PanelMaze extends JPanel {

    private final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    private final BufferedImage img;
    private final Graphics2D g2;
    private final MazeGrid GRID;
    private final Deque<MazeCell> HEAP;
    private final Random RAND;
    private final int MILISSEC;
    private boolean bShowPath;
    private final int START;
    private final int GOAL;
    private final Color VISITED = new Color(100, 100, 255);
    private final Color CURRENT = new Color(0, 255, 0);
    private final Color REVISITED = new Color(255, 90, 0);
    private final Color GRIDCOLOR = new Color(10, 10, 10);
    private final Color PATHCOLOR = new Color(200, 0, 100);
    private int chanceUp, chanceDown, chanceLeft, chanceRight;
    private int lastMove = -1;
    private boolean stop = false;

    public PanelMaze(int w, int h, int columns, int rows, int delay, long seed, boolean showPath) {
        MILISSEC = delay;
        bShowPath = showPath;
        RAND = new Random(seed);
        this.HEAP = new ArrayDeque<>();
        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g2 = (Graphics2D) img.getGraphics();
        START = randomInt(0, rows - 1);
        GOAL = randomInt(0, rows - 1);
        
        GRID = new MazeGrid(w-6, h-28, columns, rows);  // Las dimensiones del panel son menores que la ventana
    }

    public void drawMaze() {
        drawGrid();
        initChances();
        currentCell(GRID.getCell(randomInt(0, GRID.getColumns()- 1), randomInt(0, GRID.getRows()- 1)));

        while ((GRID.cellsVisited() < GRID.gridSize() || HEAP.size() > 1) && !stop) {
            if((GRID.cellsVisited() % 10)==0) {
                initChances(); }
            if (GRID.hasNeighbours(HEAP.peek())) {
                HEAP.peek().setState(MazeCell.state.VISITED);
                drawCell(HEAP.peek());                
                MazeCell nextCell = getNextCell(HEAP.peek());
                currentCell(nextCell);
            } else {
                previousCell();
            }
        }

        if(!stop) { System.out.println("Laberinto completado"); }
    }
    
    public void dispose() {
        stop = true;
        g2.dispose();
    }

    private MazeCell getNextCell(MazeCell cell) {
        int[] chance = new int[4];
        for (int f : chance) {
            f = 0;
        }

        if (GRID.validUp(HEAP.peek())) {
            if (GRID.cellUp(HEAP.peek()).getState() == MazeCell.state.UNVISITED) {
                chance[UP] = chanceUp;
            }
        }
        if (GRID.validDown(HEAP.peek())) {
            if (GRID.cellDown(HEAP.peek()).getState() == MazeCell.state.UNVISITED) {
                chance[DOWN] = chanceDown;
            }
        }
        if (GRID.validLeft(HEAP.peek())) {
            if (GRID.cellLeft(HEAP.peek()).getState() == MazeCell.state.UNVISITED) {
                chance[LEFT] = chanceLeft;
            }
        }
        if (GRID.validRight(HEAP.peek())) {
            if (GRID.cellRight(HEAP.peek()).getState() == MazeCell.state.UNVISITED) {
                chance[RIGHT] = chanceRight;
            }
        }

//        System.out.println(likelihood[0] + " " + likelihood[1] + " " + likelihood[2] + " " + likelihood[3]);    // Debug
        int total = chance[0] + chance[1] + chance[2] + chance[3];
        int index = -1;
        int choice = randomInt(1, total);

//        System.out.println("Choice: " + choice);    // Debug
        while (choice > 0 && index < 3) {   // 3 es por las posiciones 0,1,2,3 del arreglo [UP,DOWN,LEFT,RIGHT]
            index++;                        // index puede terminar desde 0 hasta 3
            choice -= chance[index];    // si la probabilidad actual es menor al valor escojido, continua al siguiente
        }

//        System.out.println("Index: " + index);  // Debug
        removeLastWall(cell);
        lastMove = index;
        switch (index) {
            case UP:
                cell.setWallUp(false);
                drawCell(cell);
                return GRID.cellUp(HEAP.peek());
            case DOWN:
                cell.setWallDown(false);
                drawCell(cell);
                return GRID.cellDown(HEAP.peek());
            case LEFT:
                cell.setWallLeft(false);
                drawCell(cell);
                return GRID.cellLeft(HEAP.peek());
            case RIGHT:
                cell.setWallRight(false);
                drawCell(cell);
                return GRID.cellRight(HEAP.peek());
            default:
                return cell;
        }
    }
    
    private void removeLastWall(MazeCell cell) {
        switch (lastMove) {
            case UP:
                cell.setWallDown(false);
                break;
            case DOWN:
                cell.setWallUp(false);
                break;
            case LEFT:
                cell.setWallRight(false);
                break;
            case RIGHT:
                cell.setWallLeft(false);
                break;
            default:
                break;
        }
    }
    
    private void initChances() {
        chanceUp = randomInt(1, 10);
        chanceDown = randomInt(1, 10);
        chanceLeft = randomInt(1, 10);
        chanceRight = randomInt(1, 10);
        
        float total = (float) (chanceDown + chanceLeft + chanceRight + chanceUp);
        float u = chanceUp/total;
        float d = chanceDown/total;
        float l = chanceLeft/total;
        float r = chanceRight/total;
        
//        System.out.println("Chances Up: " + String.format(java.util.Locale.US, "%.2f", u)
//                + "  Down: " + String.format(java.util.Locale.US, "%.2f", d)
//                + "   Left: " + String.format(java.util.Locale.US, "%.2f", l) + "   Right: "
//                + String.format(java.util.Locale.US, "%.2f", r));
    }

    private void previousCell() {
        MazeCell previous = HEAP.peek();
        removeLastWall(previous);
        lastMove = -1;              //restart value to something that doesn't belong to any wall
        previous.setState(MazeCell.state.REVISITED);
        drawCell(previous);
        HEAP.pop();
        checkPath(HEAP.peek(), previous);
        HEAP.peek().setState(MazeCell.state.CURRENT);
        drawCell(HEAP.peek());
        delay();
    }
    
    private void checkPath(MazeCell current, MazeCell previous) {
        if(previous.isPath() && current.isPath()) { bShowPath = false; }
        if(previous.isPath() && bShowPath) { current.makePath(); }        
    }
    
    private void currentCell(MazeCell cell) {
        HEAP.push(cell);  
        cell.setState(MazeCell.state.CURRENT);
        GRID.addVisited();
        drawCell(cell);
        delay();
    }
    
    private void delay() {
        try {
            Thread.sleep(MILISSEC);
        } catch (InterruptedException ex) {
            Logger.getLogger(FrameMaze.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void drawGrid() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(GRIDCOLOR);
        
        GRID.getCell(0, START).makePath();
        GRID.getCell(GRID.getColumns() - 1, GOAL).makePath();
        
        for (int i = 0; i < GRID.getColumns(); i++) {
            for (int j = 0; j < GRID.getRows(); j++) {
                GRID.getCell(i, j).setIndex(i, j);
                drawCell(GRID.getCell(i, j));
            }
        }
    }
    
    private void drawCell(MazeCell cell) {
        g2.setColor(Color.BLACK);
        g2.fillRect(cell.getXpos(), cell.getYpos(), GRID.RECT_W, GRID.RECT_H);
        switch (cell.getState()) {
            case UNVISITED:
                g2.setColor(GRIDCOLOR);
                break;
            case CURRENT:
                g2.setColor(CURRENT);
                break;
            case VISITED:
                g2.setColor(VISITED);
                break;
            case REVISITED:
                if (cell.isPath()) {
                    g2.setColor(PATHCOLOR);
                } else {
                    g2.setColor(REVISITED);
                }
                break;
        }if((cell.getIndexI()==0 && cell.getIndexJ() == START)||
                (cell.getIndexI()==GRID.getColumns()-1  && cell.getIndexJ() == GOAL)) {
            g2.setColor(Color.WHITE);
        }
        g2.fillRect(cell.getXpos() + GRID.WALL_THICKNESS, cell.getYpos() + GRID.WALL_THICKNESS,
                GRID.RECT_W - 2*GRID.WALL_THICKNESS, GRID.RECT_H - 2*GRID.WALL_THICKNESS);

        int horizontal = GRID.RECT_W-2*GRID.WALL_THICKNESS;
        int vertical = GRID.RECT_H-2*GRID.WALL_THICKNESS;
        
        if (!cell.getWallUp()) {
            g2.fillRect(cell.getXpos()+GRID.WALL_THICKNESS, cell.getYpos(),
                    horizontal, GRID.WALL_THICKNESS);
        }
        if (!cell.getWallDown()) {
            g2.fillRect(cell.getXpos()+GRID.WALL_THICKNESS, cell.getYpos() + GRID.RECT_H - GRID.WALL_THICKNESS,
                    horizontal, GRID.WALL_THICKNESS);
        }
        if (!cell.getWallLeft()) {
            g2.fillRect(cell.getXpos(), cell.getYpos()+GRID.WALL_THICKNESS,
                    GRID.WALL_THICKNESS, vertical);
        }
        if (!cell.getWallRight()) {
            g2.fillRect(cell.getXpos() + GRID.RECT_W - GRID.WALL_THICKNESS, cell.getYpos()+GRID.WALL_THICKNESS,
                    GRID.WALL_THICKNESS, vertical);
        }
                
        repaint();
    }

    private int randomInt(int lower, int upper) {
        int ent = RAND.nextInt();
        if (ent < 0) {
            ent *= -1;
        }
        return (ent % (upper - lower + 1) + lower);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
    }
}
