import java.awt.Color;

import ecs100.UI;

public class PlayerBall extends Ball {
    public PlayerBall(double x, double y) {
        super(x, y);
        this.color = Color.red;
    }

    @Override
    public void draw() {
        if (this.x < RIGHT_END) {
            UI.drawImage("player.png", this.x - (DIAM / 2), (GROUND - this.height) - DIAM);
        }
    }
}