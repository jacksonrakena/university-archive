public class TurnRNode implements ProgramNode{
    @Override
    public void execute(Robot robot) {
        robot.turnRight();
    }
    @Override
    public String toString() {
        return "";
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "Turn right";
    }
}
