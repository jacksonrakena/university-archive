// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 4
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import java.awt.Color;
import java.util.ArrayList;

import ecs100.UI;

/**
 * The program contains several methods for analysing the readings of the
 * temperature levels over the course of a day.
 * There are several things about the temperature levels that a user may be
 * interested in:
 * The average temperature level.
 * How the temperatures rose and fell over the day.
 * The maximum and the minimum temperature levels during the day.
 */
public class TemperatureAnalyser {

    public static final double HEIGHT_OF_CHAR = 14;

    public static final double WIDTH_OF_CHAR = 4;

    public static final double GRAPH_TOP_PADDING = 35;

    public static final Color GRIDLINE_GRAY = new Color(0xc4, 0xc4, 0xc4);

    /**
     * printAverage and calculateAverage both have consistent behaviour
     * between core, challenge, and completion, so they are here at the top
     * skip to line 91 to review the actual code
     */

    /**
     * Print the average level
     * - There is guaranteed to be at least one level,
     * - The method will need a variable to keep track of the sum, which
     * needs to be initialised to an appropriate value.
     * CORE
     */
    public void printAverage(ArrayList<Double> listOfNumbers) {
        double sum = 0.0;
        for (double i : listOfNumbers) {
            sum += i;
        }
        double average = sum / listOfNumbers.size();

        UI.println("Average reading: " + average);
    }

    // This method calculates the average for a list of doubles.
    public double calculateAverage(ArrayList<Double> listOfNumbers) {
        double sum = 0.0;
        for (double i : listOfNumbers) {
            sum += i;
        }
        return sum / listOfNumbers.size();
    }

    /**
     * Find and return the maximum level in the list
     * - There is guaranteed to be at least one level,
     * - The method will need a variable to keep track of the maximum, which
     * needs to be initialised to an appropriate value.
     * COMPLETION
     */
    public double maximumOfList(ArrayList<Double> listOfNumbers) {
        double maximum = listOfNumbers.get(0);
        for (Double i : listOfNumbers) {
            if (i > maximum) {
                maximum = i;
            }
        }
        return maximum;
    }

    /**
     * Find and return the minimum level in the list
     * - There is guaranteed to be at least one level,
     * - The method will need a variable to keep track of the minimum, which
     * needs to be initialised to an appropriate value.
     * COMPLETION
     */
    public double minimumOfList(ArrayList<Double> listOfNumbers) {
        double minimum = listOfNumbers.get(0);
        for (Double i : listOfNumbers) {
            if (i < minimum) {
                minimum = i;
            }
        }
        return minimum;
    }

    /**
     * THIS SECTION HANDLES THE LOGIC FOR ANALYSE (CORE)
     */

    public void analyseCore() {
        UI.clearPanes();
        UI.println("* CORE *");
        ArrayList<Double> listOfNumbers = UI.askNumbers("Enter levels, end with 'done': ");
        if (listOfNumbers.size() != 0) {
            this.plotLevelsCore(listOfNumbers);

            this.printAverage(listOfNumbers);
            UI.printf("Maximum level was:  %f\n", this.maximumOfList(listOfNumbers));
            UI.printf("Minimum level was:  %f\n", this.minimumOfList(listOfNumbers));
        } else {
            UI.println("No readings");
        }
    }

    public void plotLevelsCore(ArrayList<Double> listOfNumbers) {
        int base = 420; // base of the graph
        int left = 50; // left of the graph
        int step = 25; // distance between plotted points

        for (int i = 0; i < listOfNumbers.size(); i++) {
            double x = left + (i * step);
            double y = base;
            double reading = listOfNumbers.get(i);
            drawBarCompletion(x, y, step - 5, reading);
        }
    }

    /**
     * THIS SECTION HANDLES THE LOGIC FOR ANALYSE (COMPLETION)
     */

    public void analyseCompletion() {
        UI.clearPanes();
        UI.println("* COMPLETION *");
        ArrayList<Double> listOfNumbers = UI.askNumbers("Enter levels, end with 'done': ");
        if (listOfNumbers.size() != 0) {
            this.plotLevelsCompletion(listOfNumbers);

            this.printAverage(listOfNumbers);
            UI.printf("Maximum level was:  %f\n", this.maximumOfList(listOfNumbers));
            UI.printf("Minimum level was:  %f\n", this.minimumOfList(listOfNumbers));
        } else {
            UI.println("No readings");
        }
    }

    public void plotLevelsCompletion(ArrayList<Double> listOfNumbers) {
        int base = 420; // base of the graph
        int left = 50; // left of the graph
        int step = 25; // distance between plotted points

        UI.drawLine(left, base, left + (listOfNumbers.size() * (step)) - 5, base);

        for (int i = 0; i < listOfNumbers.size(); i++) {
            double x = left + (i * step);
            double y = base;
            double reading = listOfNumbers.get(i);
            drawBarCompletion(x, y, step - 5, reading);
        }
    }

    // (COMPLETION ONLY) Draws a bar to the plot at (x, y) with a given width and
    // height.
    public void drawBarCompletion(double x, double y, double width, double height) {
        UI.setColor(Color.black);
        double barHeight = height;
        if (height > 400) {
            UI.drawString("*", x + (width / 2), y - 405);
            barHeight = 400;
        }
        if (height < 0) {
            UI.drawString("*", x + (width / 2), y - 10);
            y = y - barHeight;
            barHeight = Math.abs(height);
        }
        UI.fillRect(x, y - barHeight, width, barHeight);
    }

    /**
     * THIS SECTION HANDLES THE LOGIC FOR ANALYSE (CHALLENGE)
     */

    /*
     * analyse reads a sequence of temperature levels from the user and prints out
     * average, maximum, and minimum level and plots all the levels
     * by calling appropriate methods
     */
    public void analyseChallenge() {
        UI.clearPanes();
        if (UI.getCanvasWidth() == 640) {
            UI.println("WARNING: Your canvas width is currently reporting as 640.");
            UI.println("An ECS100 bug means that the reported width");
            UI.println("may not be accurate if your window is smaller");
            UI.println("Than 640 pixels wide. Please make your window");
            UI.println("wider for more accurate results.");
        } else {
            UI.println("Valid canvas width detected: " + UI.getCanvasWidth());
        }
        UI.println("* CHALLENGE *");
        ArrayList<Double> listOfNumbers = UI.askNumbers("Enter levels, end with 'done': ");
        if (listOfNumbers.size() != 0) {
            this.plotLevelsChallenge(listOfNumbers);

            this.printAverage(listOfNumbers);
            UI.printf("Maximum level was:  %f\n", this.maximumOfList(listOfNumbers));
            UI.printf("Minimum level was:  %f\n", this.minimumOfList(listOfNumbers));
            UI.printf("Median level was:  %f\n", this.medianOfList(listOfNumbers));
            UI.printf("Standard deviation: %f\n", this.calculateStdDev(listOfNumbers));
            UI.printf("Range: %f\n", this.maximumOfList(listOfNumbers) - this.minimumOfList(listOfNumbers));
        } else {
            UI.println("No readings");
        }
    }

    public void plotLevelsChallenge(ArrayList<Double> listOfNumbers) {
        int base = 420; // base of the graph
        int left = 50; // left of the graph

        // Smallest reading (used to check if we should draw a negative axis)
        double minValue = minimumOfList(listOfNumbers);

        // Determine the graph range (for scaling the color)
        double range = maximumOfList(listOfNumbers) - minValue;

        // Available space on the X axis to draw
        double graphXSize = UI.getCanvasWidth() - left;

        // Available space on the Y axis to draw
        double graphYSize = (UI.getCanvasHeight() - (UI.getCanvasHeight() - base)) - GRAPH_TOP_PADDING;
        // the 'GRAPH_TOP_PADDING' is a comedically small amount of padding, otherwise
        // this is gonna look horrendous

        // How much area on the X axis each bar's drawable region (bar + gap) is allowed
        // to occupy
        double drawableAreaPerBar = ((graphXSize) / (listOfNumbers.size()));

        // The scaling coefficient, or how much to scale each bar by on the Y axis
        // This is a ratio between the graph's Y size and the largest reading
        double yBarScale = graphYSize / (maximumOfList(listOfNumbers));

        // The gap between each bar (included as part of the bar's drawable region)
        double gap = 0.1 * drawableAreaPerBar;

        // Step between labels on the Y axis
        double yAxisStep = Math.round(maximumOfList(listOfNumbers) / 10);

        // This value represents the actual, rendered size of the graph in the X
        // dimension
        double actualXSize = graphXSize - gap;

        // Every good graph has a title, right?
        String label = "Temperature Analysis";
        UI.drawString(label, left + (graphXSize / 2) - ((label.length() * WIDTH_OF_CHAR) / 2), 20);

        // Draws the gridlines on the plane
        drawGridlines(left, base, maximumOfList(listOfNumbers) * yBarScale, actualXSize, yAxisStep, yBarScale);

        for (int i = 0; i < listOfNumbers.size(); i++) {
            double x = left + (i * drawableAreaPerBar);
            double y = base;
            double reading = listOfNumbers.get(i);

            // Draw the bar, making it smaller by 'gap' to make gaps between each bar
            // '* yBarScale' scales each bar by the scaling coefficient
            drawBarChallenge(x, y, drawableAreaPerBar - gap, reading * yBarScale);
        }

        // Draws the positive Y axis, paying attention to the Y scaling coefficient
        drawPositiveYAxis(left, base, actualXSize, maximumOfList(listOfNumbers) * yBarScale, yAxisStep, yBarScale);

        // Draws the positive X axis
        drawXAxis(left, base, listOfNumbers.size(), drawableAreaPerBar, gap);

        // Draws the negative Y axis, if we have a value below zero
        if (minValue < 0)
            drawNegativeYAxis(left, base, minValue, yAxisStep, yBarScale);

        // This section draws 'plot notes' to the graph
        // (lines indicating a special value)

        // Draws the mean
        double mean = this.calculateAverage(listOfNumbers);
        drawPlotNote("Mean = " + mean, left, actualXSize, base, mean * yBarScale, Color.PINK);

        // Draws the standard deviation
        double stdDev = this.calculateStdDev(listOfNumbers);
        drawPlotNote("StdDev = " + stdDev, left, actualXSize, base, stdDev * yBarScale, Color.red);

        // This was overkill.
        // // Draw the minimum and maximum to the screen
        // drawPlotNote("Min = " + minValue, left, (listOfNumbers.size() *
        // drawableAreaPerBar), base,
        // minValue * yBarScale);
        // drawPlotNote("Max = " + maximumOfList(listOfNumbers), left,
        // (listOfNumbers.size() * drawableAreaPerBar), base,
        // maximumOfList(listOfNumbers) * yBarScale);

        // Draw the median
        drawPlotNote("Median = " + this.medianOfList(listOfNumbers), left, actualXSize,
                base, this.medianOfList(listOfNumbers) * yBarScale, Color.ORANGE);

        UI.println("Finished plotting");
    }

    public void drawGridlines(double x, double y, double height, double width, double unitStep, double scale) {
        UI.setColor(GRIDLINE_GRAY);
        for (int i = 0; i <= (height / unitStep) / scale; i++) {
            UI.drawLine(x, y - (i * unitStep * scale), x + width, y - (i * unitStep * scale));
        }
        UI.setColor(Color.black);
    }

    // This method calculates the standard deviation for a list of doubles.
    public double calculateStdDev(ArrayList<Double> listOfNumbers) {
        double mean = this.calculateAverage(listOfNumbers);
        double sum = 0.0;
        for (double i : listOfNumbers) {
            sum += Math.pow(i - mean, 2);
        }
        return Math.sqrt(sum / listOfNumbers.size());
    }

    // This method draws a given plot note (a f(x)=height line)
    // to show a specific Y value, with a given note.
    public void drawPlotNote(String text, double xBase, double widthOfXAxis, double yBase, double height, Color color) {
        UI.setColor(color);
        UI.drawLine(xBase, yBase - height, xBase + widthOfXAxis, yBase - height);
        UI.drawString(text, xBase + 5, yBase - height - 10);
        UI.setColor(Color.black);
    }

    // This method draws the Y axis with labels.
    public void drawPositiveYAxis(double x, double y, double width, double height, double unitStep, double scale) {
        UI.drawLine(x, y, x, y - height);
        UI.drawLine(x + width, y, x + width, y - height);
        for (int i = 0; i <= (height / unitStep) / scale; i++) {
            UI.drawString(Long.toString(Math.round(unitStep * i)) + "°", x - 30, y - (i * unitStep * scale));
        }
        drawVerticallySplitText("Temperature (°C)", x - 45, y - (height / 2));
    }

    // This method draws vertically split text (every character is a new line).
    // Given that ECS100 provides no way to draw text at a 90 degree rotation,
    // this is a suitable substitute.
    // It is currently unknown how this method acts on displays with different
    // resolutions.
    public void drawVerticallySplitText(String input, double x, double y) {
        char[] chars = input.toCharArray();
        double heightOfSet = HEIGHT_OF_CHAR * input.length();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            UI.drawString(Character.toString(c), x, y - (heightOfSet / 2) + (i * HEIGHT_OF_CHAR));
        }
    }

    // This method draws the negative Y axis with labels.
    public void drawNegativeYAxis(double x, double y, double maximumNegativeYValue, double step, double scale) {
        UI.drawLine(x, y, x, y + (Math.abs(maximumNegativeYValue) * scale));
        for (int i = 1; i <= (Math.abs(maximumNegativeYValue) / step); i++) {
            UI.drawString("-" + Double.toString(step * i), x - 50, y + (i * step * scale));
        }
    }

    // This method draws the X axis.
    public void drawXAxis(double x, double y, double numberOfPlots, double widthOfPlot, double gap) {
        UI.drawLine(x, y, x + (numberOfPlots * (widthOfPlot)) - gap, y);

        double xLabelDrawInterval = 1;
        if (numberOfPlots > 10)
            xLabelDrawInterval = Math.round(numberOfPlots / 10.0);
        // Draws the labels for the X axis.
        for (int i = 0; i < numberOfPlots; i++) {
            if ((i + 1) % xLabelDrawInterval == 0 || i == 0) {
                UI.drawString(Integer.toString(i + 1), x + (i * widthOfPlot) + (0.3 * widthOfPlot), y + 20);
            }
        }

        // Draws the unit label for the X axis
        String label = "Reading Number";
        double length = label.length() * WIDTH_OF_CHAR;
        UI.drawString("Reading Number", x + ((numberOfPlots * (widthOfPlot)) / 2) - (length / 2), y + 40);
    }

    // Draws a bar to the plot at (x, y) with a given width and height.
    // This function will handle negative bar heights.
    public void drawBarChallenge(double x, double y, double width, double height) {
        UI.setColor(Color.blue.darker());
        double barHeight = height;

        // If this is a negative bar, we need to draw it in positive Y (going down),
        // as opposed to negative Y (going up)
        if (height < 0) {
            UI.drawString("*", x + (width / 2), y - 10);
            y = y - barHeight;
            barHeight = Math.abs(height);
        }
        UI.fillRect(x, y - barHeight, width, barHeight);
        UI.setColor(Color.black);
    }

    // Returns the median value of a list.
    public double medianOfList(ArrayList<Double> listOfNumbers) {
        listOfNumbers.sort(null);
        if (listOfNumbers.size() % 2 == 0) {
            return (listOfNumbers.get(listOfNumbers.size() / 2) + listOfNumbers.get(listOfNumbers.size() / 2 - 1)) / 2;
        } else {
            return listOfNumbers.get(listOfNumbers.size() / 2);
        }
    }

    public void setupGUI() {
        UI.initialise();
        UI.addButton("Analyse (Core)", this::analyseCore);
        UI.addButton("Analyse (Completion)", this::analyseCompletion);
        UI.addButton("Analyse (Challenge)", this::analyseChallenge);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.3);
    }

    public static void main(String[] args) {
        TemperatureAnalyser ta = new TemperatureAnalyser();
        ta.setupGUI();
    }

}
