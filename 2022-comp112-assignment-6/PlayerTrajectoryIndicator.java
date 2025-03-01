import java.awt.Color;

import ecs100.UI;

/**
 * Shows the player a trajectory of their ball, before they fire,
 * to help them align the ball with the targets.
 */
public class PlayerTrajectoryIndicator implements Drawable {
    private double mouseX;
    private double mouseY;
    private boolean show = true;

    public PlayerTrajectoryIndicator() {
        mouseX = -1;
        mouseY = -1;
    }

    public void update(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public void show() {
        show = true;
    }

    public void hide() {
        show = false;
    }

    public void draw() {
        if (!show)
            return;
        UI.setColor(Color.green.darker());
        double speedX = (this.mouseX - BallGame.LAUNCHER_X) / 15;
        double speedUp = (BallGame.GROUND - BallGame.LAUNCHER_HEIGHT - this.mouseY) / 15;
        double speed = Math.hypot(speedUp, speedX);
        // scale down if over the maximum allowed speed
        if (speed > BallGame.MAX_SPEED) {
            speedUp = speedUp * BallGame.MAX_SPEED / speed;
            speedX = speedX * BallGame.MAX_SPEED / speed;
        }
        double oldFakeHeight = BallGame.LAUNCHER_HEIGHT;
        double oldFakeX = BallGame.LAUNCHER_X;
        double fakeHeight = BallGame.LAUNCHER_HEIGHT;
        double fakeX = BallGame.LAUNCHER_X;
        UI.printMessage(this.mouseX + "");
        for (int i = 0; i < 100; i++) {
            fakeX += speedX;

            if (fakeHeight + speedUp <= 0) {
                // this.stepY = 0;

                speedUp = Math.abs(speedUp) * 0.8;
                fakeHeight += speedUp;
                // fakeHeight = 0;
            } else {
                fakeHeight += speedUp;
                speedUp = speedUp - 0.2;
            }
            if (BallGame.GROUND - fakeHeight - speedUp < 0) {
                speedUp = speedUp * -0.7;
            }
            if (i % 3 == 0)
                UI.drawLine(oldFakeX, BallGame.GROUND - oldFakeHeight, fakeX, BallGame.GROUND - fakeHeight);
            oldFakeHeight = fakeHeight;
            oldFakeX = fakeX;
        }

        UI.setColor(Color.black);
    }
}