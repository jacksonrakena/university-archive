import ecs100.UI;

public class GameUI implements Drawable {
    private double maxTimeMilliseconds;
    private double currentTimeMsec = 0;
    private int tries = 0;

    public GameUI(double maxTimeMsec) {
        this.maxTimeMilliseconds = maxTimeMsec;
    }

    public void increment(double msec) {
        this.currentTimeMsec += msec;
    }

    public void incrementTries() {
        tries++;
    }

    public void draw() {
        UI.setFontSize(22);
        UI.drawString(((maxTimeMilliseconds - currentTimeMsec) / 1000) + "", 500, 50);
        UI.drawString(this.tries + " tries", 500, 100);
    }

    public double getTime() {
        return this.currentTimeMsec;
    }

    public void reset() {
        this.currentTimeMsec = 0;
    }
}