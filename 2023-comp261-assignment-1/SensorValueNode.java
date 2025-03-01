public class SensorValueNode extends IntegerExpressionNode {
    private final Sensor sensor;
    private final ExpressionNode<Integer> argument;
    public SensorValueNode(Sensor sensor, ExpressionNode<Integer> argument) {
        this.sensor = sensor;
        this.argument = argument;
    }
    @Override
    public Integer evaluate(Robot robot) {
        switch (sensor) {
            case FuelLeft:
                return robot.getFuel();

            case OppLR:
                return robot.getOpponentLR();

            case OppFB:
                return robot.getOpponentFB();

            case NumBarrels:
                return robot.numBarrels();

            case BarrelLR:
                if (this.argument != null) {
                    return robot.getBarrelLR(this.argument.evaluate(robot));
                }
                return robot.getClosestBarrelLR();

            case BarrelFB:
                if (this.argument != null) {
                    return robot.getBarrelFB(this.argument.evaluate(robot));
                }
                return robot.getClosestBarrelFB();

            case WallDist:
                return robot.getDistanceToWall();

            default:
                // TODO not this
                return -1;
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        StringBuilder text = new StringBuilder("Sensor(");
        text.append(this.sensor.toString());
        if (this.argument != null) {
            text.append("(").append(this.argument).append(")");
        }
        text.append(")");
        return indentationLevel + text;
    }
}
