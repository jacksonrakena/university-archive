public class TurnAroundNode implements ProgramNode{
    @Override
    public void execute(Robot robot) {
        robot.turnAround();
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "TurnAround";
    }
}
