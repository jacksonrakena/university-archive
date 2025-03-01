// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 9
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import ecs100.UI;
import ecs100.UIFileChooser;

/**
 * This program reads waveform data from a file and displays it
 * The program will also do some analysis on the data
 * The user can also edit the data - deleting, duplicating, and adding
 *
 * The methods you are to complete all focus on the ArrayList of data.
 * It is related to assignment 3 which analysed temperature levels
 *
 * CORE
 * display: displays the waveform.
 * read: reads numbers into an ArrayList.
 * showSpread: displays the maximum and minimum values of the waveform.
 * increaseRegion: increases all the values in the selected region by 10%.
 * decreaseRegion: decreases all the values in the selected region by 10%.
 * doubleFrequency: removes every second value from the waveform.
 *
 * COMPLETION
 * highlightPeaks: puts small green circles around all the peaks in the
 * waveform.
 * displayDistortion: shows in red the distorted part of the signal.
 * deleteRegion: deletes the selected region of the waveform
 * 
 * CHALLENGE
 * duplicateRegion: duplicates the selected region of the waveform
 * displayEnvelope: displays the envelope.
 * save: saves the current waveform values into a file.
 * .... allows more editing
 * 
 */

public class WaveformAnalyser {

    // Constants:
    public static final int ZERO_LINE = 300; // dimensions of the graph for the display method
    public static final int GRAPH_LEFT = 10;
    public static final int GRAPH_WIDTH = 800;
    public static final int GRAPH_RIGHT = GRAPH_LEFT + GRAPH_WIDTH;

    public static final double GRAPH_ZOOM_CONTROL_SCALE = 1.5;

    public static final double THRESHOLD = 200; // threshold for the distortion level
    public static final int CIRCLE_SIZE = 10; // size of the circles for the highlightPeaks method

    // Fields
    private ArrayList<Double> waveform; // the field to hold the ArrayList of values

    private double regionStart = 0; // The index of the first value in the selected region

    private double xAxisScale = 1;
    private double regionEnd; // The index one past the last value in the selected region
    private int viewOffset = 0;

    /**
     * Set up the user interface
     */
    public void setupGUI() {
        UI.setMouseMotionListener(this::doMouse);
        // core
        UI.addButton("Display", this::display);
        UI.addButton("Read Data", this::read);
        UI.addButton("Show Spread", this::showSpread);
        UI.addButton("Increase region", this::increaseRegion);
        UI.addButton("Decrease region", this::decreaseRegion);
        UI.addButton("Double frequency", this::doubleFrequency);
        // completion
        UI.addButton("Peaks", this::highlightPeaks);
        UI.addButton("Distortion", this::displayDistortion);
        UI.addButton("Delete", this::deleteRegion);
        // challenge
        UI.addButton("Duplicate", this::duplicateRegion);
        UI.addButton("Envelope", this::displayEnvelope);
        UI.addButton("Save", this::save);
        // challenge 2
        UI.addButton("Load .WAV", this::doLoadWavFile);
        UI.addButton("Save .WAV", this::doSaveWavFile);
        UI.addButton("Zoom in", () -> {this.xAxisScale = this.xAxisScale*GRAPH_ZOOM_CONTROL_SCALE; display();});
        UI.addButton("Zoom out", () -> {this.xAxisScale = this.xAxisScale/GRAPH_ZOOM_CONTROL_SCALE; display();});
        UI.addButton("Move right", () -> {if (this.viewOffset < this.waveform.size()-10) {this.viewOffset = this.viewOffset+10;}; display();});
        UI.addButton("Move left", () -> {if (this.viewOffset >= 10) {this.viewOffset = this.viewOffset-10;}; display();});

        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(900, 650);
    }

    /**
     * [CORE]
     * Displays the waveform as a line graph,
     * Draw the axes
     * Plots a line graph of all the points with a blue line between
     * each pair of adjacent points
     * The n'th value in waveform is at
     * x-position is GRAPH_LEFT + n
     * y-position is ZERO_LINE - the value
     * Don't worry if the data goes past the end the window
     */
    public void display() {
        if (this.waveform == null) { // there is no data to display
            UI.println("No waveform to display");
            return;
        }
        UI.clearGraphics();

        // draw x axis (showing where the value 0 will be)
        UI.setColor(Color.black);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE, GRAPH_RIGHT, ZERO_LINE);

        UI.setColor(Color.blue);
        // plot points: blue line between each pair of values
        double lastPointY = -1;
        for (int i = 0; i < this.waveform.size()-1; i++) {
            int position = viewOffset+i;
            double y = this.waveform.get(position);
            if (lastPointY == -1) {
                lastPointY = y;
                continue;
            }
            UI.drawLine(GRAPH_LEFT + xAxisScale*(i - 1), ZERO_LINE - lastPointY, GRAPH_LEFT + xAxisScale*i, ZERO_LINE - y);
            lastPointY = y;
        }

        this.displayRegion(); // Displays the selected region, if any
    }

    /**
     * [CORE]
     * Clears the panes,
     * Asks user for a waveform file (eg waveform1.txt)
     * The files consist of a sequence of numbers.
     * Creates an ArrayList stored in the waveform field, then
     * Reads data from the file into the ArrayList
     * calls display.
     */
    public void read() {
        xAxisScale = 1;
        UI.clearPanes();
        String fname = UIFileChooser.open();
        this.waveform = new ArrayList<Double>(); // create an empty list in the waveform field

        try {
            for (String input : Files.readAllLines(Path.of(fname))) {
                this.waveform.add(Double.parseDouble(input));
            }
        } catch (Exception e) {
            UI.printMessage("Failed to read " + fname + ".");
        }

        UI.printMessage("Read " + this.waveform.size() + " data points from " + fname);

        this.regionStart = 0;
        this.regionEnd = this.waveform.size();
        this.display();
    }

    /**
     * Displays the selected region by a red line on the axis
     */
    public void displayRegion() {
        UI.setColor(Color.red);
        UI.setLineWidth(3);
        UI.drawLine(GRAPH_LEFT + (this.regionStart*xAxisScale), ZERO_LINE, GRAPH_LEFT + (this.regionEnd*xAxisScale) - 1, ZERO_LINE);
        UI.setLineWidth(1);
    }

    /**
     * [CORE]
     * The spread is the difference between the maximum and minimum values of the
     * waveform.
     * Finds the maximum and minimum values of the waveform, then
     * Displays the spread by drawing two horizontal lines on top of the waveform:
     * one green line for the maximum value, and
     * one red line for the minimum value.
     */
    public void showSpread() {
        if (this.waveform == null) { // there is no data to display
            UI.println("No waveform to display");
            return;
        }
        this.display();

        UI.setColor(Color.green);
        double highestValue = Collections.max(this.waveform);
        double lowestValue = Collections.min(this.waveform);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE - highestValue, GRAPH_RIGHT, ZERO_LINE - highestValue);

        UI.setColor(Color.red);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE - lowestValue, GRAPH_RIGHT, ZERO_LINE - lowestValue);
    }

    /**
     * Mutates the selected region by performing the specified action on each sound value,
     * and setting that sound value to the action result.
     *
     * The provided action must return a non-null Double.
     */
    public void editRegionByValue(Function<Double, Double> action) {
        for (int i = 0; i < this.waveform.size(); i++) {
            if (i > this.regionStart && i < this.regionEnd) {
               Double newDouble = action.apply(this.waveform.get(i));
               this.waveform.set(i, newDouble);
            }
        }
    }

    /**
     * [CORE]
     * Increases the values in the selected region of the waveform by 10%.
     * (The selected region is initially the whole waveform, but the user can drag
     * the
     * mouse over part of the graph to select a smaller region).
     * The selected region goes from the index in the regionStart field to the index
     * in the regionEnd field.
     */
    public void increaseRegion() {
        if (this.waveform == null) { // there is no waveform to process
            UI.println("No waveform");
            return;
        }
        editRegionByValue(value -> value * 1.1);

        this.display();
    }

    /**
     * [CORE]
     * Decreases the values in the selected region of the waveform by 10%.
     * (The selected region is initially the whole waveform, but the user can drag
     * the
     * mouse over part of the graph to select a smaller region).
     * The selected region goes from the index in the regionStart field to the index
     * in the regionEnd field.
     */
    public void decreaseRegion() {
        if (this.waveform == null) { // there is no waveform to process
            UI.println("No waveform");
            return;
        }
        /* # YOUR CODE HERE */
        editRegionByValue(value -> value * 0.9);

        this.display();
    }

    /**
     * [CORE]
     * Double the frequency of the waveform by removing every second value in the
     * list.
     * Resets the selected region to the whole waveform
     */
    public void doubleFrequency() {
        if (this.waveform == null) { // there is no waveform to process
            UI.println("No waveform");
            return;
        }
        ArrayList<Double> newWaveform = new ArrayList<>(this.waveform.size() / 2);
        for (int i = 0; i < this.waveform.size(); i++) {
            if (i % 2 == 0)
                newWaveform.add(this.waveform.get(i));
        }
        this.waveform = newWaveform;

        this.display();
    }

    public LinkedHashMap<Integer, Double> findPositivePeaks() {
        LinkedHashMap<Integer, Double> hm = new LinkedHashMap<>();
        for (int i = 1; i < this.waveform.size(); i++) {
            double current = this.waveform.get(i);
            boolean isPeak = false;
            if (current > this.waveform.get(i - 1)) {
                if (!(i + 1 >= this.waveform.size())) {
                    if (current > this.waveform.get(i + 1)) {
                        isPeak = true;
                    }
                } else {
                    isPeak = true;
                }
            }

            if (isPeak) {
                hm.put(i, current);
            }
        }
        return hm;
    }

    public LinkedHashMap<Integer, Double> findNegativePeaks() {
        LinkedHashMap<Integer, Double> hm = new LinkedHashMap<>();
        for (int i = 2; i < this.waveform.size(); i++) {
            double current = this.waveform.get(i);
            boolean isPeak = false;
            if (current < this.waveform.get(i - 2)) {
                if (!(i + 2 >= this.waveform.size())) {
                    if (current < this.waveform.get(i + 2) && this.waveform.get(i + 2) < 0) {
                        isPeak = true;
                    }
                } else {
                    isPeak = true;
                }
            }

            if (isPeak) {
                hm.put(i, current);
            }
        }
        return hm;
    }

    /**
     * [COMPLETION]
     * Plots the peaks with small green circles.
     * A peak is defined as a value that is greater than or equal to both its
     * neighbouring values.
     * Note the size of the circle is in the constant CIRCLE_SIZE
     */
    public void highlightPeaks() {
        this.display(); // use display if displayDistortion isn't complete

        LinkedHashMap<Integer, Double> positivePeaks = findPositivePeaks();
        UI.setColor(Color.green);
        for (int i : positivePeaks.keySet()) {
            UI.drawOval(GRAPH_LEFT + i - CIRCLE_SIZE / 2, ZERO_LINE - positivePeaks.get(i) - (CIRCLE_SIZE / 2),
                    CIRCLE_SIZE, CIRCLE_SIZE);
        }
    }

    /**
     * [COMPLETION] [Fancy version of display]
     * Display the waveform as a line graph.
     * Draw a line between each pair of adjacent points
     * * If neither of the points is distorted, the line is BLUE
     * * If either of the two end points is distorted, the line is RED
     * Draw the horizontal lines representing the value zero and thresholds values.
     * Uses THRESHOLD to determine distorted values.
     * Uses GRAPH_LEFT and ZERO_LINE for the dimensions and positions of the graph.
     * [Hint] You may find Math.abs(int a) useful for this method.
     * You may assume that all the values are between -250 and +250.
     */
    public void displayDistortion() {
        if (this.waveform == null) { // there is no data to display
            UI.println("No waveform to display");
            return;
        }
        UI.clearGraphics();

        // draw zero axis
        UI.setColor(Color.black);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE, GRAPH_LEFT + (this.waveform.size()*xAxisScale), ZERO_LINE);

        double lastPointY = -1;
        for (int i = 0; i < this.waveform.size(); i++) {
            UI.setColor(Color.blue);
            double y = this.waveform.get(i);
            if (lastPointY == -1) {
                lastPointY = y;
                continue;
            }
            double negativeThreshold = 0 - THRESHOLD;
            if (lastPointY > THRESHOLD || y > THRESHOLD || lastPointY < negativeThreshold || y < negativeThreshold)
            {
                UI.setColor(Color.red);
            }
            UI.drawLine(GRAPH_LEFT + ((xAxisScale)*(i - 1)), ZERO_LINE - lastPointY, GRAPH_LEFT + (xAxisScale*i), ZERO_LINE - y);
            lastPointY = y;
        }

        UI.setColor(Color.green);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE-THRESHOLD, GRAPH_LEFT+(this.waveform.size()*xAxisScale), ZERO_LINE-THRESHOLD);
        UI.drawLine(GRAPH_LEFT, ZERO_LINE+THRESHOLD, GRAPH_LEFT+(this.waveform.size()*xAxisScale), ZERO_LINE+THRESHOLD);

        this.displayRegion();
    }

    /**
     * [COMPLETION]
     * Removes the selected region from the waveform
     * selection should be reset to be the whole waveform
     * redisplays the waveform
     */
    public void deleteRegion() {
        ArrayList<Double> newWaveform = new ArrayList<>((int) (this.waveform.size()-(regionEnd-regionStart)));
        for (int i = 0; i < this.waveform.size(); i++) {
            if (i < regionStart || i > regionEnd)
                newWaveform.add(this.waveform.get(i));
        }
        this.waveform = newWaveform;
        this.regionStart = 0;
        this.regionEnd = this.waveform.size();

        this.display();
    }

    /**
     * [CHALLENGE]
     * If there is a selected region, then add a copy of that section to the
     * waveform,
     * immediately following the selected region
     * selection should be reset to be the whole waveform
     * redisplay the waveform
     */
    public void duplicateRegion() {
        if (this.regionEnd != this.waveform.size()) {
            ArrayList<Double> spliced = new ArrayList<>();
            for (int i = 0; i < this.waveform.size(); i++) {
                if (i > regionStart && i < regionEnd) {
                    spliced.add(this.waveform.get(i));
                }
            }
            this.waveform.addAll((int)this.regionEnd, spliced);
        }

        this.display();
    }

    /**
     * [CHALLENGE]
     * Displays the envelope (upper and lower) with GREEN lines connecting all the
     * peaks.
     * A peak is defined as a point that is greater than or equal to *both*
     * neighbouring points.
     */
    public void displayEnvelope() {
        if (this.waveform == null) { // there is no data to display
            UI.println("No waveform to display");
            return;
        }
        this.display(); // display the waveform

        drawConnectedLine(findPositivePeaks());
        drawConnectedLine(findNegativePeaks());
    }

    public void drawConnectedLine(LinkedHashMap<Integer, Double> line) {
        double lastX = Double.NEGATIVE_INFINITY;
        double lastY = Double.NEGATIVE_INFINITY;
        UI.setColor(Color.green.darker());
        for (int i : line.keySet()) {
            double y = line.get(i);
            if (lastY == Double.NEGATIVE_INFINITY) {
                lastX = i;
                lastY = y;
                continue;
            }
            UI.drawLine(GRAPH_LEFT + (lastX*xAxisScale), ZERO_LINE - lastY, GRAPH_LEFT + (i*xAxisScale), ZERO_LINE - y);
            lastX = i;
            lastY = y;
        }
    }

    public void doLoadWavFile() {
        xAxisScale = 10;
        this.waveform = new ArrayList<>(WaveformLoader.doLoad().stream().map(e -> 10000*e).collect(Collectors.toList()));
        System.out.println(this.waveform.size());
        System.out.println(this.waveform.get(0));
        display();
    }

    public void doSaveWavFile() {
        WaveformLoader.doSave(this.waveform, 10000);
    }

    /**
     * [CHALLENGE]
     * Saves the current waveform values into a file
     */
    public void save() {
        String outputFile = UIFileChooser.save("Where to save new waveform:");
        if (outputFile == null) return;
        try {
            Files.writeString(Path.of(outputFile), this.waveform.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())));
            UI.printMessage("Saved " + this.waveform.size() + " data points to " + outputFile);
        } catch (IOException e) {
            UI.printMessage("Couldn't save the waveform: " + e.getMessage());
        }
    }

    /**
     * Lets user select a region of the waveform with the mouse
     * and deletes that section of the waveform.
     */
    public void doMouse(String action, double x, double y) {
        double index = ((x-GRAPH_LEFT)/xAxisScale);
        if (action.equals("pressed")) {
            this.regionStart = Math.max(index, 0);
        } else if (action.equals("released")) {
            if (index < this.regionStart) {
                this.regionEnd = this.regionStart;
                this.regionStart = Math.max(index, this.waveform.size());
            } else {
                this.regionEnd = Math.min(index, this.waveform.size());
            }
            this.display();
        }

    }

    /**
     * Make a "triangular" waveform file for testing the other methods
     */
    public void makeTriangleWaveForm() {
        this.waveform = new ArrayList<Double>();
        for (int cycle = 0; cycle < 10; cycle++) {
            for (int i = 0; i < 15; i++) {
                this.waveform.add(i * 18.0);
            }
            for (int i = 15; i > -15; i--) {
                this.waveform.add(i * 18.0);
            }
            for (int i = -15; i < 0; i++) {
                this.waveform.add(i * 18.0);
            }
        }
        this.regionStart = 0;
        this.regionEnd = this.waveform.size();
    }

    public static void main(String[] args) {
        WaveformAnalyser wav = new WaveformAnalyser();
        wav.setupGUI();
        wav.makeTriangleWaveForm();
    }
}
