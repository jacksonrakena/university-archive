public class TurnLNode implements ProgramNode{
    @Override
    public void execute(Robot robot) {
        robot.turnLeft();
    }
    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "Turn left";
    }
}
