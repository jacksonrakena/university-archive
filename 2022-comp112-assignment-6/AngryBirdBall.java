import ecs100.UI;

public class AngryBirdBall extends EnemyBall {

    public AngryBirdBall(double x, double y) {
        super(x, y);
    }

    @Override
    public void draw() {
        if (this.x < RIGHT_END) {
            UI.drawImage("pig.png", this.x - (DIAM / 2), (GROUND - this.height) - DIAM);
        }
    }
}
