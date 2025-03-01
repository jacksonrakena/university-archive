import java.awt.Color;

import ecs100.UI;

public class Shelf implements Drawable {
    private double x;
    private double height;
    private double width;
    private Color color;

    public Shelf(double x, double height, double width, Color color) {
        this.x = x;
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public void draw() {
        UI.setColor(this.color);
        UI.drawLine(this.x, (Ball.GROUND - this.height), this.x + this.width, (Ball.GROUND - this.height));
        UI.setColor(Color.black);
    }
}