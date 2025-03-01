public class SetShieldNode implements ProgramNode{
    private final boolean setTo;

    public SetShieldNode(boolean setTo) {
        this.setTo = setTo;
    }

    @Override
    public void execute(Robot robot) {
        robot.setShield(setTo);
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "SetShield["+setTo+"]";
    }
}
