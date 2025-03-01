// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2022T2, Online test
 * Name:
 * Username:
 * ID:
 */

/**
 * Question 7. Writing methods with for loop. Reading files.
 *
 * This program should read numbers from the "numbers.txt" file and store the numbers in a List<Double> variable. 
 * After that the numbers are plot in a diagram.
 * 
 * The plotNumbers() method is done for you. It calls two methods that you must complete.
 * 
 * (a) [5 marks] Complete the readNumbers() method. It must,
 *                   - read the numbers from the "numbers.txt" file.
 *                   - add each number to List<Double> result.
 * 
 * (b) [10 marks] Complete the plotNumbers(...) method to draw a line plot of the numbers
 *                   - Draw the x-axis and the y-axis. The origin should be at (50, 50)
 *                   - Draw the points every 50 units (down the y-axis), plotting the data along the x-axis
 *                     (note the orientation of the plot on Blackboard)
 *                   - You should start the line plot from the origin
 *                   - You may assume the file contains at least one line and at most nine lines. 
 *                   - The graph should only plot numbers in the range of [0, 300]
 *                   - A negative number should be plot as 0, with a circle (diameter 10) around it.
 *                   - A number higher than 300 should be plot as 300, with a circle (diameter 10) around it.
 */
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ecs100.UI;

public class Program2 {
    /**
     * Constants
     */
    private static final double X_AXIS = 50;
    private static final double Y_AXIS = 50;
    private static final double STEP = 50;
    private static final double DIAMETER = 10;

    public void plotNumbers() {
        List<Double> numbers = this.readNumbers();
        this.plotNumbers(numbers);
    }

    public List<Double> readNumbers() {
        List<Double> result = new ArrayList<Double>();

        try {
            Scanner inputScanner = new Scanner(new File("numbers.txt"));
            while (inputScanner.hasNext()) {
                Double number = inputScanner.nextDouble();
                result.add(number);
            }
        } catch (FileNotFoundException e) {
            UI.println("File not found");
        }
        return result;
    }

    public void plotNumbers(List<Double> numbers) {
        UI.clearGraphics();

        UI.setColor(Color.red);
        UI.setLineWidth(2);

        UI.drawLine(50, 50, 350, 50);

        UI.drawLine(50, 50, 50, 50 + (numbers.size() * Y_AXIS));

        UI.setColor(Color.black);

        double lastX = 0;
        for (int i = 0; i < numbers.size(); i++) {
            Double number = numbers.get(i);

            double y = Y_AXIS + (STEP * i);
            double x = number;

            if (number < 0) {
                UI.drawOval(X_AXIS - (DIAMETER / 2), (Y_AXIS + y) - (DIAMETER / 2), DIAMETER, DIAMETER);
                x = 0;
            } else if (number > 300) {
                x = 300;
                UI.drawOval((x + 50) - (DIAMETER / 2), (Y_AXIS + y) - (DIAMETER / 2), DIAMETER, DIAMETER);
            }
            UI.drawLine(X_AXIS + lastX, y, X_AXIS + x, Y_AXIS + y);
            lastX = x;
        }
    }

    /*********************************************
     * YOU CAN IGNORE EVERYTHING BELOW THIS LINE *
     *********************************************/
    public void setupGUI() {
        UI.initialise();
        UI.addButton("Run", this::plotNumbers);
        UI.setWindowSize(800, 500);
        UI.setDivider(0.3);
    }

    public static void main(String[] args) {
        new Program2().setupGUI();
    }
}
