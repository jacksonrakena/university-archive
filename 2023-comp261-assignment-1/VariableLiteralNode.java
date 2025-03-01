public class VariableLiteralNode extends IntegerExpressionNode{

    private final BlockNode parent;
    private final String name;

    public VariableLiteralNode(BlockNode parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public Integer evaluate(Robot robot) {
        return this.parent.resolveVariable(this.name);
    }

    @Override
    public String toString(String indentationLevel) {
        return this.name;
    }
    @Override
    public String toString() { return toString("");}
}
