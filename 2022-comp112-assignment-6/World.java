import java.awt.Color;

import ecs100.UI;

public class World implements Drawable {
    public void draw() {
        UI.setColor(new Color(227, 211, 230));
        UI.fillRect(0, 0, UI.getCanvasWidth(), UI.getCanvasHeight());
        UI.setColor(Color.darkGray);
        UI.drawLine(BallGame.LAUNCHER_X, BallGame.GROUND, BallGame.RIGHT_END, BallGame.GROUND);
        UI.drawLine(BallGame.RIGHT_END, BallGame.GROUND, BallGame.RIGHT_END, 0);
        UI.setColor(Color.black);
    }
}