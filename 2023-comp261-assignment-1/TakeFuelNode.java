public class TakeFuelNode implements ProgramNode{
    @Override
    public String toString() {
        return toString("");
    }
    @Override
    public void execute(Robot robot) {
        robot.takeFuel();
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "TakeFuel";
    }
}
