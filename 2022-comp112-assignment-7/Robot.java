// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 7
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;

import ecs100.UI;

/**
 * The robot is a circular vacuum cleaner than runs around
 * a floor, erasing any "dirt".
 * The parameters of the constructor should include the initial position,
 * and possibly its size and color.
 * It has methods to make it step and change direction:
 * step() makes it go forward one step in its current direction.
 * changeDirection() makes it go backward one step, and then turn to a new
 * (random) direction.
 * It has methods to report its current position (x and y) with the
 * getX() and getY() methods.
 * It has methods to erase and draw itself
 * draw() will make it draw itself,
 * erase() will make it erase itself (cleaning the floor under it also!)
 *
 * Hint: if the current direction of the robot is d (expressed in
 * degrees clockwise from East), then it should step
 * cos(d * pi/180) in the horizontal direction, and
 * sin(d * pi/180) in the vertical direction.
 * Hint: see the Math class documentation!
 */

public class Robot {
    // The diameter of the robot
    private final double diameter;

    private double getRadius() {
        return this.diameter / 2;
    }

    // The X position of the center of the robot
    private double positionX;

    // The Y position of the center of the robot
    private double positionY;

    // The direction that the robot is facing, in degrees
    private double directionDeg;

    // The robot's color
    private final Color color;

    /**
     * Construct a new Robot object.
     * set its direction to a random direction, 0..360
     */
    public Robot(double diam, double xpos, double ypos, Color color, double startingAngle) {
        this.diameter = diam;
        this.positionX = xpos;
        this.positionY = ypos;
        this.color = color;
        this.directionDeg = startingAngle;
    }

    public Robot(double diam, double xpos, double ypos, Color color) {
        this.diameter = diam;
        this.positionX = xpos;
        this.positionY = ypos;
        this.color = color;
        this.directionDeg = Math.floor(Math.random() * 360.0);
    }

    // Methods to return the x and y coordinates of the current position

    /** Step one unit in the current direction (but don't redraw) */
    public void step() {
        /* # YOUR CODE HERE */
        this.positionX += Math.cos(this.directionDeg * (Math.PI / 180));
        this.positionY += Math.sin(this.directionDeg * (Math.PI / 180));
    }

    /** changeDirection: move backwards one unit and change direction randomly */
    public void changeDirection() {
        /* # YOUR CODE HERE */
        this.positionX -= Math.cos(this.directionDeg * (Math.PI / 180));
        this.positionY -= Math.sin(this.directionDeg * (Math.PI / 180));
        this.directionDeg = Math.floor(Math.random() * 360);
    }

    /** Erase the robot */
    public void erase() {
        UI.eraseOval(this.positionX - this.getRadius(), this.positionY - this.getRadius(), this.diameter,
                this.diameter);
    }

    public double getX() {
        return this.positionX;
    }

    public double getY() {
        return this.positionY;
    }

    public void setX(double x) {
        this.positionX = x;
    }

    public void setY(double y) {
        this.positionY = y;
    }

    public double getDirection() {
        return this.directionDeg;
    }

    public void setDirection(double angle) {
        this.directionDeg = angle;
    }

    /** Draw the robot */
    public void draw() {
        UI.setColor(color);
        UI.fillOval(this.positionX - this.getRadius(), this.positionY - this.getRadius(), this.diameter, this.diameter);
        UI.setColor(Color.black);
        UI.drawOval(this.positionX - this.getRadius(), this.positionY - this.getRadius(), this.diameter, this.diameter);
        UI.fillOval(this.positionX + ((0.8 * this.getRadius()) * Math.cos(this.directionDeg * Math.PI / 180)),
                this.positionY + ((0.8 * this.getRadius()) * Math.sin(this.directionDeg * Math.PI / 180)), 5, 5);
    }
}
