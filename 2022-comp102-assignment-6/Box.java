import java.awt.Color;

import ecs100.UI;

public class Box implements Collidable, Drawable {
    private double x;
    private double heightFromGround;
    private final double cubeWidth;
    private final double cubeHeight;
    private Color color;
    private double xSpeed;
    private double ySpeed;

    public Box(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.heightFromGround = y;
        this.cubeWidth = width;
        this.cubeHeight = height;
        this.color = color;
    }

    public void draw() {
        UI.setColor(this.color.darker());
        UI.fillRect(this.x, BallGame.GROUND - this.heightFromGround - this.cubeHeight, this.cubeWidth,
                this.cubeHeight);
        UI.setColor(Color.black);

        UI.setLineWidth(2);
        UI.drawRect(this.x, BallGame.GROUND - this.heightFromGround - this.cubeHeight, this.cubeWidth,
                this.cubeHeight);
        UI.drawLine(this.x, BallGame.GROUND - this.heightFromGround, this.x + this.cubeWidth,
                BallGame.GROUND - this.heightFromGround - this.cubeHeight);
        UI.drawLine(this.x + this.cubeWidth, BallGame.GROUND - this.heightFromGround, this.x,
                BallGame.GROUND - this.heightFromGround - this.cubeHeight);

        UI.resetLineWidth();
    }

    public double getX() {
        return this.x;
    }

    public double getHeight() {
        return this.heightFromGround;
    }

    public boolean isInBounds() {
        return this.x >= 0 && this.x + this.cubeWidth <= BallGame.RIGHT_END && this.heightFromGround >= 0
                && this.heightFromGround + this.cubeHeight <= BallGame.GROUND;
    }

    public boolean isMoving() {
        return (this.getXSpeed() != 0) || (this.getYSpeed() != 0);
    }

    public void step() {
        this.x += this.xSpeed;

        if (this.heightFromGround + this.ySpeed <= 0) {
            // this.stepY = 0;
            this.ySpeed = Math.abs(this.ySpeed) * 0.1;
            this.heightFromGround = 0;
            this.xSpeed = Math.abs(this.xSpeed) * 0.5;
        } else {
            this.heightFromGround += this.ySpeed;
            this.ySpeed = this.ySpeed - 0.2;
        }
    }

    public void setXSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public double getXSpeed() {
        return this.xSpeed;
    }

    public double getYSpeed() {
        return this.ySpeed;
    }

}