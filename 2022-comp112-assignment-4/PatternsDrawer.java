// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 4
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import java.awt.Color;

import ecs100.UI;

/**
 * PatternsDrawer:
 * Draws four different repetitive patterns.
 */

public class PatternsDrawer {

    public static final double PATTERN_LEFT = 50.0; // Left side of the pattern
    public static final double PATTERN_TOP = 50.0; // Top of the pattern
    public static final double PATTERN_SIZE = 300.0; // The size of the pattern on the window

    /**
     * Draws a row pattern consisting of a row of filled rectangles,
     * alternating between the colours blue and orange
     * Asks the user for the number of rectangles.
     */
    public void drawRow() {
        UI.clearGraphics();
        double num = UI.askInt("How many rectangles:");
        for (int i = 0; i < num; i++) {
            UI.setColor(i % 2 == 0 ? Color.blue : Color.orange);
            UI.fillRect(PATTERN_LEFT + i * PATTERN_SIZE / num, PATTERN_TOP, PATTERN_SIZE / num, PATTERN_SIZE / num);
        }
    }

    /**
     * Draw a checkered board with alternating black and white squares
     * Asks the user for the number of squares on each side
     *
     * CORE
     */
    public void drawDraughtsBoard() {
        UI.clearGraphics();
        UI.setColor(Color.black);
        int num = UI.askInt("How many rows:");
        for (int row = 0; row < num; row++) {
            for (int col = 0; col < num; col++) {
                // We don't need to draw white squares, because the background is white.
                if ((row % 2 == 0 && col % 2 == 0) || (row % 2 == 1 && col % 2 == 1))
                    continue;

                UI.fillRect(PATTERN_LEFT + col * PATTERN_SIZE / num, PATTERN_TOP + row * PATTERN_SIZE / num,
                        PATTERN_SIZE / num, PATTERN_SIZE / num);
            }
        }
    }

    /**
     * TriGrid
     * a triangular grid of squares that makes dark circles appear
     * in the intersections when you look at it.
     *
     * COMPLETION
     */
    public void drawTriGrid() {
        UI.clearGraphics();
        UI.setColor(Color.black);
        int num = UI.askInt("How many rows:");
        double heightOfRow = PATTERN_SIZE / num;
        double gap = 0.1 * heightOfRow;
        // double widthOfSquareInRow

        for (int row = 0; row < num; row++) {
            for (int col = 0; col < (num - row); col++) {
                UI.fillRect(PATTERN_LEFT + col * (heightOfRow), PATTERN_TOP + row * heightOfRow, heightOfRow - gap,
                        heightOfRow - gap);
            }
        }
    }

    // Challenge: Concentric circles
    public void drawConcentricCircles() {
        UI.clearGraphics();
        UI.setColor(Color.gray);
        int num = UI.askInt("How many rows and columns:");

        double width = PATTERN_SIZE / num;
        for (int row = 0; row < num; row++) {
            for (int col = 0; col < num; col++) {
                drawConcentricCircle(PATTERN_LEFT + (width * col), PATTERN_LEFT + (width * row), width);
            }
        }
    }

    // Challenge:
    // This method draws a concentric circle (a circle with many circles in it)
    // at a given (x, y) coordinate with a starting width.
    public void drawConcentricCircle(double x, double y, double width) {
        UI.setColor(Color.gray);
        UI.drawOval(x, y, width, width);
        double diffScale = 0.015 * width;
        boolean draw = true;
        int diff = 1;
        while (draw) {
            UI.drawOval(x + (diff * diffScale), y + (diff * diffScale), width - diff * diffScale * 2,
                    width - diff * diffScale * 2);
            diff++;
            if ((width - diff * diffScale * 2) < 3) {
                draw = false;
            }
        }
    }

    public void setupGUI() {
        UI.initialise();
        UI.setDivider(0.3);
        UI.addButton("Clear", UI::clearPanes);
        UI.addButton("Core: row", this::drawRow);
        UI.addButton("Core: draughts", this::drawDraughtsBoard);
        UI.addButton("Completion: TriGrid", this::drawTriGrid);
        UI.addButton("Challenge: Concentric Circles", this::drawConcentricCircles);
        UI.addButton("Quit", UI::quit);
    }

    public static void main(String[] arguments) {
        PatternsDrawer pd = new PatternsDrawer();
        pd.setupGUI();
    }

}
