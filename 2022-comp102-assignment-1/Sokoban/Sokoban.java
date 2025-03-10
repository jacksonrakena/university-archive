// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;

/** 
 * Sokoban
 */

public class Sokoban {

    private final Stack<ActionRecord> history = new Stack<>();
    private final Stack<ActionRecord> redo = new Stack<>();
    private Cell[][] cells;             // the array representing the warehouse
    private int rows;                   // the height of the warehouse
    private int cols;                   // the width of the warehouse
    private int level = 1;              // current level 

    private Position workerPos;         // the position of the worker
    private String workerDir = "left";  // the direction the worker is facing


    /** 
     *  Constructor: load the 0th level.
     */
    public Sokoban() {
        doLoad();
    }

    /** 
     *  Moves the worker in the given direction, if possible.
     *  If there is box in front of the Worker and a space in front of the box,
     *  then push the box.
        workerDir = direction;                       // turn worker to face in this direction

     *  Otherwise, if the worker can't move, do nothing.
                */
    public void moveOrPush(String direction) {
        Position nextP = workerPos.next(direction);  // where the worker would move to
        Position nextNextP = nextP.next(direction);  // where a box would be pushed to

        // is there a box in that direction which can be pushed?
        if ( cells[nextP.row][nextP.col].hasBox() &&
        cells[nextNextP.row][nextNextP.col].isFree() ) { 
            push(direction);
            history.push(new ActionRecord("push", direction));

            if (isSolved()) { reportWin(); }
        }
        // is the next cell free for the worker to move into?
        else if ( cells[nextP.row][nextP.col].isFree() ) { 
            move(direction);
            history.push(new ActionRecord("move", direction));
        }
    }

    /**
     * Moves the worker into the new position (guaranteed to be empty) 
     * @param direction the direction the worker is heading
     */
    public void move(String direction) {
        drawCell(workerPos);                   // redisplay cell under worker
        workerPos = workerPos.next(direction); // put worker in new position
        drawWorker();                          // display worker at new position

        Trace.println("Move " + direction);    // for debugging
    }

    /**
     * Push: Moves the Worker, pushing the box one step 
     *  @param direction the direction the worker is heading
     */
    public void push(String direction) {
        Position boxPos = workerPos.next(direction);   // where box is
        Position newBoxPos = boxPos.next(direction);   // where box will go

        cells[boxPos.row][boxPos.col].removeBox();     // remove box from current cell
        cells[newBoxPos.row][newBoxPos.col].addBox();  // place box in its new position

        drawCell(workerPos);                           // redisplay cell under worker
        drawCell(boxPos);                              // redisplay cell without the box
        drawCell(newBoxPos);                           // redisplay cell with the box

        workerPos = boxPos;                            // put worker in new position
        drawWorker();                                  // display worker at new position

        Trace.println("Push " + direction);   // for debugging
    }

    /**
     * Pull: (could be useful for undoing a push)
     *  move the Worker in the direction,
     *  pull the box into the Worker's old position
     */
    public void pull(String direction) {
        Position oldBoxPos = workerPos.next(opposite(direction));
        Position newBoxPos = workerPos;
        move(direction);
        cells[oldBoxPos.row][oldBoxPos.col].removeBox();
        cells[newBoxPos.row][newBoxPos.col].addBox();
        drawCell(oldBoxPos);
        drawCell(newBoxPos);
        drawWorker();
    }

    public void undo() {
        if (history.empty()) return;
        ActionRecord lastAction = history.pop();
        redo.push(lastAction);
        if (lastAction.isPush()) pull(opposite(lastAction.direction()));
        else if (lastAction.isMove()) move(opposite(lastAction.direction()));
        else if (lastAction.getAction().equals("teleport")) {
            UI.printMessage("Teleporting back to " + lastAction.getPreviousPositionForTeleport());
            workerPos = lastAction.getPreviousPositionForTeleport();
            drawWarehouse();
        }
    }

    public void redo() {
        if (redo.empty()) return;
        ActionRecord redoAction = redo.pop();
        history.push(redoAction);
        if (redoAction.isPush()) push(redoAction.direction());
        else if (redoAction.isMove()) move(redoAction.direction());
        else if (redoAction.getAction().equals("teleport")) {
            UI.printMessage("Re-doing a teleport to " + redoAction.getNextPositionForTeleport());
            workerPos = redoAction.getNextPositionForTeleport();
            drawWarehouse();
        }
    }

    public boolean isWarpable(Position current, Position destination) {
        List<Position> toVisit = new ArrayList<Position>(determinePossibleDirections(current));
        List<Position> visited = new ArrayList<>();
        visited.add(current);

        while (toVisit.size() > 0) {
            visited.add(current);
            if (destination.row == current.row && destination.col == current.col) {
                return true;
            }
            current = toVisit.remove(0);
            List<Position> possible = determinePossibleDirections(current).stream().filter(g -> visited.stream().noneMatch(h -> g.row == h.row && g.col == h.col)).collect(Collectors.toList());
            toVisit.addAll(possible);
        }
        return false;
    }

    public void replay(List<Position> warps) {
        UI.println(warps);
        for (Position move : warps) {
            workerPos = move;
            drawWarehouse();
            UI.sleep(400);
        }
    }

    public void warp(Position current, Position destination) {
        List<NodePosition> toVisit = determinePossibleDirections(current).stream().map(d -> new NodePosition(d, 2)).collect(Collectors.toList());
        List<NodePosition> visited = new ArrayList<>();

        NodePosition cur = new NodePosition(current, 1);

        while (toVisit.size() > 0) {
            visited.add(cur);
            UI.println("cur:" + cur + " value: " + cur.value);
            if (destination.row == cur.row && destination.col == cur.col) {
                UI.println("d cur:" + cur + " value: " + cur.value);
                return;
            }
            cur = toVisit.remove(0);
            NodePosition finalCur = cur;
            List<NodePosition> possible = determinePossibleDirections(cur)
                    .stream()
                    .filter(g -> visited.stream().noneMatch(h -> g.row == h.row && g.col == h.col))
                    .map(n -> new NodePosition(n, finalCur.value+1))
                    .collect(Collectors.toList());
            UI.println("cur:" + cur + " value: " + cur.value + " moves: " + (long) possible.size());
            toVisit.addAll(possible);
        }
        return;
    }

    public List<Position> determinePossibleDirections(Position current) {
        String[] directions = new String[] {"right", "left", "up", "down"};
        return Arrays.stream(directions).map(current::next).filter(this::isValidPosition).collect(Collectors.toList());
    }

    public boolean isValidPosition(Position toCheck) {
        return (toCheck.row < cells.length && toCheck.col < cells[toCheck.row].length) && cells[toCheck.row][toCheck.col].isFree();
    }

    /**
     * Report a win by flickering the cells with boxes
     */
    public void reportWin(){
        for (int i=0; i<12; i++) {
            for (int row=0; row<cells.length; row++)
                for (int column=0; column<cells[row].length; column++) {
                    Cell cell=cells[row][column];

                    // toggle shelf cells
                    if (cell.hasBox()) {
                        cell.removeBox();
                        drawCell(row, column);
                    }
                    else if (cell.isEmptyShelf()) {
                        cell.addBox();
                        drawCell(row, column);
                    }
                }

            UI.sleep(100);
        }
    }

    /** 
     *  Returns true if the warehouse is solved, 
     *  i.e., all the shelves have boxes on them 
     */
    public boolean isSolved() {
        for(int row = 0; row<cells.length; row++) {
            for(int col = 0; col<cells[row].length; col++)
                if(cells[row][col].isEmptyShelf())
                    return  false;
        }

        return true;
    }

    /** 
     * Returns the direction that is opposite of the parameter
     * useful for undoing!
     */
    public String opposite(String direction) {
        if ( direction.equals("right")) return "left";
        if ( direction.equals("left"))  return "right";
        if ( direction.equals("up"))    return "down";
        if ( direction.equals("down"))  return "up";
        throw new RuntimeException("Invalid  direction");
    }


    // Drawing the warehouse
    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 40;
    private static final int CELL_SIZE = 25;

    /**
     * Draw the grid of cells on the screen, and the Worker 
     */
    public void drawWarehouse() {
        UI.clearGraphics();
        // draw cells
        for(int row = 0; row<cells.length; row++)
            for(int col = 0; col<cells[row].length; col++)
                drawCell(row, col);

        drawWorker();
    }

    /**
     * Draw the cell at a given position
     */
    private void drawCell(Position pos) {
        drawCell(pos.row, pos.col);
    }

    /**
     * Draw the cell at a given row,col
     */
    private void drawCell(int row, int col) {
        double left = LEFT_MARGIN+(CELL_SIZE* col);
        double top = TOP_MARGIN+(CELL_SIZE* row);
        cells[row][col].draw(left, top, CELL_SIZE);
    }

    /**
     * Draw the worker at its current position.
     */
    private void drawWorker() {
        double left = LEFT_MARGIN+(CELL_SIZE* workerPos.col);
        double top = TOP_MARGIN+(CELL_SIZE* workerPos.row);
        UI.drawImage("worker-"+workerDir+".gif",
            left, top, CELL_SIZE,CELL_SIZE);
    }

    /**
     * Load a grid of cells (and Worker position) for the current level from a file
     */
    public void doLoad() {
        history.clear();
        redo.clear();
        Path path = Path.of("warehouse" + level + ".txt");

        if (! Files.exists(path)) {
            UI.printMessage("Run out of levels- Back to Level 1!");
            UI.sleep(100);
            level = 1;
            doLoad();
            return;
        }
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        }
        catch(IOException e) {
            UI.println("File error: " + e);
            return;
        }

        int rows = lines.size();
        cells = new Cell[rows][];

        for(int row = 0; row < rows; row++) {
            String line = lines.get(row);
            int cols = line.length();
            cells[row]= new Cell[cols];
            for(int col = 0; col < cols; col++) {
                char ch = line.charAt(col);
                if (ch=='w'){
                    cells[row][col] = new Cell("empty");
                    workerPos = new Position(row,col);
                }
                else if (ch=='.') cells[row][col] = new Cell("empty");
                else if (ch=='#') cells[row][col] = new Cell("wall");
                else if (ch=='s') cells[row][col] = new Cell("shelf");
                else if (ch=='b') cells[row][col] = new Cell("box");
                else {
                    throw new RuntimeException("Invalid char at "+row+","+col+"="+ch);
                }
            }
        }
        drawWarehouse();
        UI.printMessage("Level "+level+": Push the boxes to their target positions. Use buttons or put mouse over warehouse and use keys (arrows, wasd, ijkl, u)");
    }

    /**
     * Add the buttons and set the key listener.
     */
    public void setupGUI(){
        UI.addButton("New Level",       () -> {level++; doLoad();});
        UI.addButton("Restart Level",   this::doLoad);
        UI.addButton("left",            () -> {moveOrPush("left");});
        UI.addButton("up",              () -> {moveOrPush("up");});
        UI.addButton("down",            () -> {moveOrPush("down");});
        UI.addButton("right",           () -> {moveOrPush("right");});
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Quit",            UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setKeyListener(this::doKey);
        UI.setDivider(0.0);
    }

    public void doMouse(String s, double x, double y) {
        if (s.equals("released")) {
            double realX = x - LEFT_MARGIN;
            double realY = y - TOP_MARGIN;
            double col = realX/CELL_SIZE;
            double row = realY/CELL_SIZE;
            Position pos = new Position((int) row, (int) col);

            //if (isWarpable(workerPos, pos)) {
                history.push(new ActionRecord("teleport", "up", workerPos, pos));
                warp(workerPos, pos);
              //  workerPos = pos;
                UI.printMessage("Quick-travelled to " + workerPos.toString());
                drawWarehouse();
            //} else {
                UI.printMessage("Can't travel there!");
            //}
        }
    }

    /** 
     * Respond to key actions
     */
    public void doKey(String key) {
        key = key.toLowerCase();
        if (key.equals("i")|| key.equals("w") ||key.equals("up")) {
            moveOrPush("up");
        }
        else if (key.equals("k")|| key.equals("s") ||key.equals("down")) {
            moveOrPush("down");
        }
        else if (key.equals("j")|| key.equals("a") ||key.equals("left")) {
            moveOrPush("left");
        }
        else if (key.equals("l")|| key.equals("d") ||key.equals("right")) {
            moveOrPush("right");
        }
        else if (key.equals("u")) {
            undo();
        }
        else if (key.equals("r")) {
            redo();
        }
    }

    public static void main(String[] args) {
        Sokoban skb = new Sokoban();
        skb.setupGUI();
    }
}
