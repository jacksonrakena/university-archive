// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import ecs100.UI;
import ecs100.UIFileChooser;

public class GraphPlotter {

    // Constants for plotting the graph
    public static final double GRAPH_LEFT = 50;
    public static final double GRAPH_RIGHT = 550;
    public static final double GRAPH_BASE = 400;

    /**
     * Plot a graph of a sequence of numbers read from a file using +'s for each
     * point.
     * The origin of the graph should be at (GRAPH_LEFT, GRAPH_BASE)
     * The method should ask the user for the name of a file that contains only
     * numbers
     * It should then plot the numbers:
     * - Draw two axes
     * - Plot each number as a small + eg, to plot at (x,y),
     * draw a line from (x-2,y) to (x+2,y) and a line from (x,y-2) to (x,y+2)
     * - The x value of the first point should be at GRAPH_LEFT, and
     * the last point should be at GRAPH_RIGHT.
     * - (ie, the points should be separated by (GRAPH_RIGHT - GRAPH_LEFT)/(number
     * of points - 1)
     * Hints:
     * look at the model answers for the Temperature Analyser problem from
     * assignment 3.
     */
    public void plotGraph() {
        UI.clearPanes();
        try {
            List<Double> readings = Files.readAllLines(Path.of(UIFileChooser.open("Numbers"))).stream()
                    .map(Double::parseDouble).collect(Collectors.toList());
            int numberOfReadings = readings.size();
            double width = (GRAPH_RIGHT - GRAPH_LEFT);
            double gap = width / (numberOfReadings - 1);

            // Draw axes
            UI.drawLine(GRAPH_LEFT, GRAPH_BASE, GRAPH_LEFT, 0); // Y
            UI.drawLine(GRAPH_LEFT, GRAPH_BASE, GRAPH_LEFT + width, GRAPH_BASE); // X

            for (int i = 0; i < numberOfReadings; i++) {
                double reading = GRAPH_BASE - readings.get(i);
                double x = GRAPH_LEFT + (gap * i);
                UI.drawLine(x - 2, reading, x + 2, reading);
                UI.drawLine(x, reading - 2, x, reading + 2);
            }

        } catch (IOException e) {
            UI.println("File reading failed");
        }
    }

    /** set up the buttons */
    public void setupGUI() {
        UI.addButton("Clear", UI::clearPanes);
        UI.addButton("Plot", this::plotGraph);
        UI.addButton("quit", UI::quit);
        UI.setDivider(0.0);
    }

    public static void main(String[] args) {
        GraphPlotter gp = new GraphPlotter();
        gp.setupGUI();
    }
}
