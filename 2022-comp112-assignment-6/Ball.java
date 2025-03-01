// This program is copyright VUW.

// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 6
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

/**
 * Ball represents a ball that is launched by the mouse towards a direction.
 * Each time the step() method is called, it will take one step.
 * For the Completion part, gravity should act on the ball by reducing its
 * vertical speed.
 */

import java.awt.Color;
import java.util.Random;

import ecs100.UI;

public class Ball implements Collidable, Drawable {

    // Constants for all balls: size, position of the ground
    public static final double DIAM = 16; // diameter of the balls.
    public static final double GROUND = BallGame.GROUND;
    public static final double RIGHT_END = BallGame.RIGHT_END;

    // Fields to store state of the ball:
    // x position, height above ground, stepX, stepY, colour
    // The ball should initially be not moving at all. (step should be 0)
    /* # YOUR CODE HERE */
    protected double x;
    protected double height;
    protected double stepX;
    protected double stepY;
    protected Color color;

    // Constructor
    /**
     * Construct a new Ball object.
     * Parameters are the initial position (x and the height above the ground),
     * Stores the parameters into fields
     * and initialises the colour to a random colour
     * SHOULD NOT DRAW THE BALL!
     */
    public Ball(double x, double h) {
        this.x = x;
        this.height = h;
        Random r = new Random();
        this.color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
        this.stepX = 0;
        this.stepY = 0;
    }

    // Methods
    /**
     * Draw the ball on the Graphics Pane in its current position
     * (unless it is past the RIGHT_END )
     */
    public void draw() {
        if (this.x < RIGHT_END) {
            UI.drawOval(this.x - (DIAM / 2), (GROUND - this.height) - DIAM, DIAM, DIAM);
            UI.setColor(this.color);
            UI.fillOval(this.x - (DIAM / 2), (GROUND - this.height) - DIAM, DIAM, DIAM);
            UI.setColor(Color.black);
        }
    }

    /**
     * Move the ball one step (DO NOT REDRAW IT)
     * Core:
     * Change its height and x position using the vertical and horizonal steps
     * Completion:
     * Reduce its vertical speed each step (due to gravity),
     * If it would go below ground, then change its y position to ground level and
     * set the vertical speed back to 0.
     */
    public void step() {
        this.x += this.stepX;

        if (BallGame.GROUND - this.height - this.stepY < 0) {
            this.stepY = this.stepY * -0.7;
        }

        if (this.height + this.stepY <= 0) {
            // this.stepY = 0;
            this.stepY = Math.abs(this.stepY) * 0.8;
            this.height = 0;
        } else {
            this.height += this.stepY;
            this.stepY = this.stepY - 0.2;
        }
    }

    public boolean isMoving() {
        return (this.getXSpeed() != 0) || (this.getYSpeed() != 0);
    }

    public double getXSpeed() {
        return this.stepX;
    }

    public double getYSpeed() {
        return this.stepY;
    }

    /**
     * Set the horizontal speed of the ball: how much it moves to the right in each
     * step.
     * (negative if ball going to the left).
     */
    public void setXSpeed(double xSpeed) {
        this.stepX = xSpeed;
    }

    /**
     * Set the vertical speed of the ball: how much it moves up in each step
     * (negative if ball going down).
     */
    public void setYSpeed(double ySpeed) {
        this.stepY = ySpeed;
    }

    /**
     * Return the height of the ball above the ground
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Return the horizontal position of the ball
     */
    public double getX() {
        return this.x;
    }

    public boolean isInBounds() {
        return this.x < BallGame.RIGHT_END;
    }

    public void setPosition(double x, double height) {
        this.x = x;
        this.height = height;
    }
}
