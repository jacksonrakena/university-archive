// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 7
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ecs100.UI;

/**
 * Runs a simulation of a robot vacuum cleaner that moves around a floor area,
 * changing to a new random direction every time it hits the edge of the
 * floor area.
 */
public class FloorCleaner {

    // Constants for drawing the robot and the floor.
    public static final double DIAM = 60; // diameter of the robot.
    public static final double LEFT = 50; // borders of the floor area.
    public static final double TOP = 50;
    public static final double RIGHT = 550;
    public static final double BOT = 420;
    public static Random random = new Random();


    /**
     * Checks if the robot is out of the bounds of the floor,
     * and if it is, moves it back into the play area.
     * @param robot The robot to check.
     */
    public void checkBoundsCollision(Robot robot) {
        if (robot.getX() > (RIGHT - DIAM / 2)) {
            robot.setX(RIGHT - DIAM / 2);
            robot.setDirection(180 + Math.floor(Math.random() * 180));
        }
        if (robot.getX() < (LEFT + DIAM / 2)) {
            robot.setX(LEFT + DIAM / 2);
            robot.setDirection(-180 + Math.floor(Math.random() * 180));
        }

        if (robot.getY() > (BOT - DIAM / 2)) {
            robot.setY(BOT - DIAM / 2);
            robot.setDirection(0 - Math.floor(Math.random() * 180));
        }

        if (robot.getY() < (TOP + DIAM / 2)) {
            robot.setY(TOP + DIAM / 2);
            robot.setDirection(0 + Math.floor(Math.random() * 180));
        }
    }
    /*
     * Simulation loop.
     * The method should draw a dirty floor (a gray rectangle), and then
     * create one robot (core) or two robots (completion) and make them run around
     * for ever.
     * Each time step, each robot will erase the "dirt" under it, and then
     * move forward a small amount.
     * After it has moved, the program should ask for the robot's
     * position and check the position against the edges of the floor area.
     * If it has gone over the edge, it will make the robot step back onto the floor
     * and change its direction.
     * For the completion, it will also check if the robots have hit each other, and
     * if so, make them both back off and change direction
     * 
     * Hint: A robot should start in a "safe" initial position (not over the edge):
     * its x position should be between LEFT+DIAM/2 and RIGHT-DIAM/2
     * its y position should be between TOP+DIAM/2 and BOT-DIAM/2
     * Hint: For the completion, you have to make sure that starting positions of
     * the robots are not on top of each other (otherwise they get "stuck" to each
     * other!)
     */
    public void cleanFloorCore() {
        Robot robot = createRobot();
        UI.setColor(Color.gray);
        UI.fillRect(FloorCleaner.LEFT, FloorCleaner.TOP, FloorCleaner.RIGHT - FloorCleaner.LEFT,
                FloorCleaner.BOT - FloorCleaner.TOP);

        while (true) {
            // Remove the background where the robot is (cleaning)
            robot.erase();
            robot.step();

            // Check the robot's collision against the walls of the area
            checkBoundsCollision(robot);
            robot.draw();
            UI.sleep(30);
        }
    }

    /**
     * Renders two robots, and runs the simulation, including showing their angle indicators.
     */
    public void cleanFloorCompletion() {
        // Create our two robots
        List<Robot> robots = Arrays.asList(createRobot(), createRobot());
        UI.setColor(Color.gray);
        UI.fillRect(FloorCleaner.LEFT, FloorCleaner.TOP, FloorCleaner.RIGHT - FloorCleaner.LEFT,
                FloorCleaner.BOT - FloorCleaner.TOP);

        while (true) {
            for (Robot robot : robots) {
                // Remove the background where the robot is (cleaning)
                robot.erase();
                robot.step();

                // Check collision against the boundary of the area (floor)
                checkBoundsCollision(robot);

                // Check collision against the other robot(s) in the scene
                for (Robot otherRobot : robots.stream().filter(e -> e != robot).collect(Collectors.toList())) {
                    if (Math.hypot(robot.getX() - otherRobot.getX(), robot.getY() - otherRobot.getY()) <= DIAM) {
                        robot.setDirection(-180 + Math.floor(Math.random() * 180));
                        otherRobot.setDirection(0 + Math.floor(Math.random() * 180));
                    }
                }
                robot.draw();
            }
            UI.sleep(30);
        }
    }

    /**
     * Runs the simulation with one robot with a pre-planned movement sequence,
     * and furniture blocking it's path.
     */
    public void cleanFloorChallenge() {
        Robot robot = new Robot(DIAM, FloorCleaner.LEFT + (DIAM / 2),
                FloorCleaner.BOT - (DIAM / 2), new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()), 0);
        List<Furniture> objects = Arrays.asList(new Furniture(200, TOP + (DIAM/2), DIAM),new Furniture(400, TOP + (DIAM/2), DIAM));
        UI.setColor(Color.gray);
        UI.fillRect(FloorCleaner.LEFT, FloorCleaner.TOP, FloorCleaner.RIGHT - FloorCleaner.LEFT,
                FloorCleaner.BOT - FloorCleaner.TOP);

        while (true) {
            for (Furniture furn : objects) {
                furn.draw();
            }
            robot.erase();
            boolean stepThisFrame = true;


            for (Furniture furn : objects) {
                if (Math.hypot(robot.getX() - furn.getX(), robot.getY() - furn.getY()) <= DIAM) {
                    robot.changeDirection();
                    stepThisFrame = false;
                }
            }

            // If we hit the right side, go up and then go left
            if (robot.getX() > (RIGHT - DIAM / 2)) {
                double originalY = robot.getY();
                robot.setX(RIGHT - DIAM /2);
                robot.setDirection(-90);

                // Keep stepping up until we reach the next line
                while (robot.getY() > originalY - DIAM) {
                    robot.erase();
                    robot.step();
                    robot.draw();
                    UI.sleep(30);
                }

                // Go in a straight line to the left
                robot.setDirection(-180);
            }

            // If we hit the left side, go up and then go right
            if (robot.getX() < (LEFT + DIAM / 2)) {
                double originalY = robot.getY();
                robot.setX((LEFT + DIAM / 2));
                robot.setDirection(-90);

                // Keep stepping up until we reach the next line
                while (robot.getY() > originalY - DIAM) {
                    robot.erase();
                    robot.step();
                    robot.draw();
                    UI.sleep(30);
                }

                // Go in a straight line to the right
                robot.setDirection(0);
            }

            // Check against top and bottom
            checkBoundsCollision(robot);

            if (stepThisFrame) robot.step();

            robot.draw();
            UI.sleep(30);
        }
    }

    /**
     * This method creates a robot in a random position with a random color and random direction,
     * checking to make sure that the robot will spawn within the bounds of the area.
     * @return A new, randomly positioned robot instance.
     */
    public Robot createRobot() {
        double x = (LEFT + DIAM / 2) + Math.floor(Math.random() * (RIGHT - LEFT));
        if (x > (RIGHT - DIAM / 2))
            x = RIGHT - DIAM / 2;
        double y = (TOP + DIAM / 2) + Math.floor(Math.random() * (BOT - TOP));
        if (y > (BOT - DIAM / 2))
            y = BOT - DIAM / 2;
        return new Robot(DIAM, x, y, new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
    }

    // ------------------ Set up the GUI (buttons) ------------------------
    /** Make buttons to let the user run the methods */
    public void setupGUI() {
        UI.addButton("Core", this::cleanFloorCore);
        UI.addButton("Completion", this::cleanFloorCompletion);
        UI.addButton("Challenge", this::cleanFloorChallenge);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(650, 500);
        UI.setDivider(0);
    }

    // Main
    public static void main(String[] arguments) {
        FloorCleaner fc = new FloorCleaner();
        fc.setupGUI();
    }

}
