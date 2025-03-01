// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 3
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import java.awt.Color;
import java.util.Random;

import javax.swing.JColorChooser;

import ecs100.UI;

/**
 * Parameterised Shapes:
 * Core and Completion: draw flags with three horizontal stripes
 * Challenge: draw a street of houses
 */
public class ParameterisedShapes {

    // Constants for CORE and COMPLETION (three stripes flags)
    public static final double FLAG_WIDTH = 200;
    public static final double FLAG_HEIGHT = 133;

    /**
     * CORE
     * Asks user for a position and three colours, then calls the
     * drawThreeStripesFlag method, passing the appropriate arguments
     */
    public void doSimpleFlag() {
        UI.clearGraphics();
        double left = UI.askDouble("Left of flag");
        double top = UI.askDouble("Top of flag");
        UI.println("Now choose the colours");
        Color stripe1 = JColorChooser.showDialog(null, "First Stripe", Color.white);
        Color stripe2 = JColorChooser.showDialog(null, "Second Stripe", Color.white);
        Color stripe3 = JColorChooser.showDialog(null, "Third Stripe", Color.white);
        this.drawThreeStripesFlag(left, top, stripe1, stripe2, stripe3);
    }

    /**
     * CORE
     * Draws a three colour flag at the given position consisting of
     * three equal size stripes of the given colors
     * The stripes are horizontal.
     * The size of the flag is specified by the constants FLAG_WIDTH and FLAG_HEIGHT
     */
    public void drawThreeStripesFlag(double x, double y, Color topStripe, Color middleStripe, Color bottomStripe) {
        UI.clearGraphics();

        UI.setColor(topStripe);
        UI.fillRect(x, y, FLAG_WIDTH, FLAG_HEIGHT / 3);
        UI.setColor(middleStripe);
        UI.fillRect(x, y + (FLAG_HEIGHT * (1.0 / 3)), FLAG_WIDTH, FLAG_HEIGHT / 3);
        UI.setColor(bottomStripe);
        UI.fillRect(x, y + (FLAG_HEIGHT * (2.0 / 3)), FLAG_WIDTH, FLAG_HEIGHT / 3);
    }

    /**
     * COMPLETION
     * Asks user for a position, three colours, three heights and whether the
     * circles are filled.
     * Then calls the drawFancyFlag method, passing the appropriate arguments
     */
    public void doFancyFlag() {
        UI.clearGraphics();
        double left = UI.askDouble("Left of flag");
        double top = UI.askDouble("Top of flag");
        UI.println("Now choose the colours");
        Color col1 = JColorChooser.showDialog(null, "First Stripe", Color.white);
        Color col2 = JColorChooser.showDialog(null, "Second Stripe", Color.white);
        Color col3 = JColorChooser.showDialog(null, "Third Stripe", Color.white);
        UI.println("Now choose the sizes in pixels.");
        double stripe1Height = UI.askDouble("First stripe:");
        double stripe2Height = UI.askDouble("Second stripe:");
        double stripe3Height = UI.askDouble("Third stripe:");
        boolean filled = UI.askBoolean("Filled?");
        drawFancyFlag(left, top, col1, col2, col3, stripe1Height, stripe2Height, stripe3Height, filled);
    }

    /**
     * COMPLETION
     * Calculates the total height and width of the flag.
     * The width of the flag is 1.5 times the height of the flag.
     * It then calls drawStripe three times to draw the three stripes,
     * and outlines the flag with a black rectangle.
     */
    public void drawFancyFlag(double x, double y, Color stripe1, Color stripe2, Color stripe3, double stripe1Height,
            double stripe2Height, double stripe3Height, boolean filled) {
        double width = (stripe1Height + stripe2Height + stripe3Height) * 1.5;
        UI.clearGraphics();
        UI.drawRect(x, y, width, stripe1Height + stripe2Height + stripe3Height);

        drawStripe(stripe1, x, y, stripe1Height, width, x + (width / 3) / 2, filled);
        drawStripe(stripe2, x, y + (stripe1Height), stripe2Height, width, x + (width / 2), filled);
        drawStripe(stripe3, x, y + (stripe1Height) + (stripe2Height),
                stripe3Height, width, x + ((width / 3) * 2) + (width / 3) / 2, filled);
    }

    /**
     * COMPLETION
     * Draws a stripe at the given position that has the right circle at the right
     * place.
     */
    public void drawStripe(Color color, double x, double y, double height, double width, double circleX,
            boolean filled) {
        UI.setColor(color);
        UI.fillRect(x, y, width, height);
        UI.setColor(Color.black);
        if (filled)
            UI.fillOval(circleX - (0.2 * height) / 2, y + (height / 2) - (0.2 * height) / 2, 0.2 * height,
                    0.2 * height);
        else
            UI.drawOval(circleX - (0.2 * height) / 2, y + (height / 2) - (0.2 * height) / 2, 0.2 * height,
                    0.2 * height);
    }

    /**
     * Draws a street (a collection of houses) to the screen.
     * 
     * @param x              The x position to begin drawing.
     * @param y              The y position to begin drawing.
     * @param numberOfHouses The number of houses to draw.
     * @param width          The width of the street.
     * @param height         The height of the street.
     */
    public void drawStreet(double x, double y, int numberOfHouses, Color houseFrameColor, Color windowFrameColor) {
        double widthPerHouse = 100;
        for (int i = 0; i < numberOfHouses; i++) {
            drawHouse(x + (widthPerHouse * i), y, widthPerHouse * 0.9, 400, houseFrameColor, windowFrameColor);
        }
    }

    /**
     * Draws a single house to the screen.
     * 
     * @param x      The x position to begin drawing.
     * @param y      The y position to begin drawing.
     * @param width  The width of the house.
     * @param height The height of the house.
     */
    public void drawHouse(double x, double y, double width, double height, Color houseFrameColor,
            Color windowFrameColor) {
        int numberOfWindows = new Random().nextInt(5);
        if (numberOfWindows == 0)
            numberOfWindows = 1;
        double heightOfTopTriangle = height * 0.1;
        double heightOfStructure = height * 0.9;

        double triangleBase = y + heightOfTopTriangle;

        UI.setColor(houseFrameColor);
        UI.drawRect(x, triangleBase, width, heightOfStructure);

        double widthOfWindow = 0.8 * width;
        double heightPerWindow = 40;
        double yBase = triangleBase + (heightOfStructure * 0.1);
        double xBase = x + (0.1 * width);

        drawRoof(x, y, width, heightOfTopTriangle);
        for (int i = 0; i < numberOfWindows; i++) {
            drawWindow(xBase + (widthOfWindow * 0.1), yBase + (i * (heightPerWindow)), widthOfWindow * 0.8,
                    heightPerWindow * 0.8, windowFrameColor);
        }
    }

    /**
     * Draws a triangular roof (of a house) to the screen.
     * 
     * @param x      The x position of the roof.
     * @param y      The y position of the roof.
     * @param width  The width of the roof.
     * @param height The height of the roof.
     */
    public void drawRoof(double x, double y, double width, double height) {
        UI.drawPolygon(new double[] {
                x,
                x + (width / 2),
                x + width
        }, new double[] {
                y + height,
                y,
                y + height
        }, 3);
    }

    /**
     * Draws a window to the screen.
     * 
     * @param x      The x position of the window.
     * @param y      The y position of the window.
     * @param width  The width of the window.
     * @param height The height of the window.
     */
    public void drawWindow(double x, double y, double width, double height, Color windowFrameColor) {
        UI.setColor(windowFrameColor);
        UI.drawRect(x, y, width, height);
        UI.drawRect(x, y, width, height / 2);
        UI.drawRect(x, y, width / 2, height);
    }

    public void doChallenge() {
        UI.clearGraphics();
        int houseNumber = UI.askInt("Number of houses: ");
        Color houseFrameColor = JColorChooser.showDialog(null, "Choose the color of the houses:", Color.white);
        Color windowFrameColor = JColorChooser.showDialog(null, "Choose the color of the windows:", Color.white);

        drawStreet(20, 20, houseNumber, houseFrameColor, windowFrameColor);
    }

    public void setupGUI() {
        UI.initialise();
        UI.addButton("Clear", UI::clearPanes);
        UI.addButton("Simple Flag", this::doSimpleFlag);
        UI.addButton("Fancy Flag", this::doFancyFlag);
        UI.addButton("Draw Street", this::doChallenge);
        // Add a button here to call your method for the challenge part
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    public static void main(String[] args) {
        ParameterisedShapes ps = new ParameterisedShapes();
        ps.setupGUI();
    }

}
