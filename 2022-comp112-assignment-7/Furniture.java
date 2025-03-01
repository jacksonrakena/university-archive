import java.awt.Color;

import ecs100.UI;

public class Furniture {
    public Furniture(double x, double y, double diam) {
        this.color = new Color(FloorCleaner.random.nextFloat(), FloorCleaner.random.nextFloat(),
                FloorCleaner.random.nextFloat());
        this.x = x;
        this.y = y;
        this.diam = diam;
    }

    private final double x;
    private final double y;
    private final double diam;
    private final Color color;

    public double getDiam() {
        return this.diam;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void draw() {
        UI.setColor(Color.black);
        UI.drawOval(this.x - (this.diam / 2), this.y - (this.diam / 2), diam, diam);
        UI.setColor(this.color);
        UI.fillOval(this.x - (this.diam / 2), this.y - (this.diam / 2), diam, diam);
    }
}
