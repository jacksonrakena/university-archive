import ecs100.UI;

public class PlayerLauncher implements Drawable {
    public void draw() {
        UI.drawLine(BallGame.LAUNCHER_X, BallGame.GROUND, BallGame.LAUNCHER_X,
                BallGame.GROUND - BallGame.LAUNCHER_HEIGHT);
        UI.drawLine(BallGame.LAUNCHER_X - Ball.DIAM / 2, BallGame.GROUND -
                BallGame.LAUNCHER_HEIGHT,
                BallGame.LAUNCHER_X + Ball.DIAM / 2,
                BallGame.GROUND - BallGame.LAUNCHER_HEIGHT);
    }
}