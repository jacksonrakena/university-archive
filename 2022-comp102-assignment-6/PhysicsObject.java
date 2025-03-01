
public interface PhysicsObject extends GameObject {
    public double getX();

    public void step();

    public void setXSpeed(double xSpeed);

    public void setYSpeed(double ySpeed);

    public double getXSpeed();

    public double getYSpeed();

    public double getHeight();

    public boolean isMoving();

    public boolean isInBounds();
}