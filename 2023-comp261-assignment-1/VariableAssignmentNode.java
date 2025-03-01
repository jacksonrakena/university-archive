public class VariableAssignmentNode implements ProgramNode{
    private final String name;
    private final BlockNode parent;
    private final ExpressionNode<Integer> value;

    public VariableAssignmentNode(BlockNode parent, String name, ExpressionNode<Integer> value) {
        this.name = name;
        this.parent = parent;
        this.value = value;
    }

    @Override
    public void execute(Robot robot) {
        parent.setVariable(name, value.evaluate(robot));
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + this.name + " = " + value;
    }

    @Override
    public String toString() {
        return toString("");
    }
}
