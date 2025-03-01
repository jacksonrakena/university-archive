// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/** Pencil   */
public class Pencil{
    private Point2D.Double origin;

    public PencilAction currentAction;
    public Stack<PencilAction> actionHistory = new Stack<>();
    public Stack<PencilAction> redoActions = new Stack<>();

    public PencilState currentState;
    public JButton undoButton;

    public JButton colorButton;
    public JButton redoButton;

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);

        undoButton = UI.addButton("Undo", this::doUndo);
        undoButton.setVisible(false);
        redoButton = UI.addButton("Redo", this::doRedo);
        redoButton.setVisible(false);

        colorButton = UI.addButton("Color", () -> {
            applyState(new PencilState(currentState.strokeWidth, JColorChooser.showDialog(UI.getFrame(), "Color", currentState.strokeColor)));
        });
        UI.addSlider("Stroke Width", 1, 10, 1, c -> {
            applyState(new PencilState(c, currentState.strokeColor));
        });

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
        applyDefaultState();
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        switch (action) {
            case "pressed":
                // Initialise a new action.
                currentAction = new PencilAction(currentState.clone());
                origin = new Point2D.Double(x, y);
                break;
            case "dragged":
                // Draw the stroke, and add the stroke to the current action.
                drawLineWithCurrentState(origin, new Point2D.Double(x, y));
                currentAction.addStroke(new PartialStroke(origin, new Point2D.Double(x, y)));

                // Update the buttons.
                redoButton.setVisible(false);
                undoButton.setVisible(true);
                origin = new Point2D.Double(x, y);
                break;
            case "released":
                redoActions.clear();
                // Store the last stroke, and save the action to the stack.
                currentAction.addStroke(new PartialStroke(origin, new Point2D.Double(x, y)));
                UI.printMessage("Drew " + currentAction.strokes.size() + " strokes");
                drawLineWithCurrentState(origin, new Point2D.Double(x, y));
                actionHistory.add(currentAction);
                break;
            default:
                break;
        }
    }

    /**
     * Applies the currently stored pencil state, and then draws a line
     * from the provided origin to the destination.
     * @param origin A 2D co-ordinate representing the origin.
     * @param destination A 2D co-ordinate representing the destination.
     */
    public void drawLineWithCurrentState(Point2D.Double origin, Point2D.Double destination) {
        UI.setLineWidth(currentState.strokeWidth);
        UI.setColor(currentState.strokeColor);
        UI.drawLine(origin.getX(), origin.getY(), destination.getX(), destination.getY());
    }

    /**
     * Stores and applies a provided pencil state.
     * @param state The pencil state to apply.
     */
    public void applyState(PencilState state) {
        currentState = state.clone();
        colorButton.setForeground(state.strokeColor);
    }

    /**
     * Replays an action, re-drawing all the component strokes.
     * @param action The action to replay.
     */
    public void replayAction(PencilAction action) {
        applyState(action.state);
        for (PartialStroke stroke : action.strokes) {
            drawLineWithCurrentState(stroke.origin, stroke.destination);
        }
    }

    /**
     * Updates the buttons on the input panel according to the current internal state.
     */
    public void renderButtonState() {
        undoButton.setVisible(!actionHistory.isEmpty());
        redoButton.setVisible(!redoActions.isEmpty());
    }

    /**
     * Applies the default state of a 1-pixel black pencil.
     */
    public void applyDefaultState() {
        applyState(new PencilState(1, Color.black));
    }

    /**
     * Undoes the previous action.
     */
    public void doUndo() {
        if (actionHistory.isEmpty()) return;

        // Pop the action of the stack.
        PencilAction target = actionHistory.pop();

        // Clear the screen.
        UI.clearGraphics();

        // Re-draw all remaining actions.
        for (PencilAction action : actionHistory) {
            replayAction(action);
        }

        UI.printMessage("Removed " + target.strokes.size() + " strokes and set the pencil state to " + this.currentState.toString());

        redoActions.push(target);
        renderButtonState();
    }

    public void doRedo() {
        if (redoActions.isEmpty()) return;
        // Pop the action off this stack.
        PencilAction target = redoActions.pop();
        applyState(target.state.clone());

        // Redraw all lines, including the newly re-did action.
        for (PartialStroke stroke: target.strokes) {
            drawLineWithCurrentState(stroke.origin, stroke.destination);
        }
        actionHistory.push(target);

        UI.printMessage("Redrew " + target.strokes.size() + " strokes and set the pencil state to " + this.currentState.toString());
        renderButtonState();
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
