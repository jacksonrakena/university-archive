public class ConstantIntegerNode extends IntegerExpressionNode {
    private final int value;

    public ConstantIntegerNode(int value) {
        this.value = value;
    }

    @Override
    public Integer evaluate(Robot robot) {
        return this.value;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indentationLevel) {
        return indentationLevel + Integer.toString(this.value);
    }
}
