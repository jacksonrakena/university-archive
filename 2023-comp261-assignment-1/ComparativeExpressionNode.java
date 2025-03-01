public class ComparativeExpressionNode extends BooleanExpressionNode {
    private final RelationalOperator operator;
    private final ExpressionNode<Integer> lhs;
    private final ExpressionNode<Integer> rhs;

    public ComparativeExpressionNode(RelationalOperator operator, ExpressionNode<Integer> lhs, ExpressionNode<Integer> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Boolean evaluate(Robot robot) {
        int lhsResolved = this.lhs.evaluate(robot);
        int rhsResolved = this.rhs.evaluate(robot);
        switch (this.operator) {
            case GreaterThan:
                return lhsResolved > rhsResolved;

            case LessThan:
                return lhsResolved < rhsResolved;

            case Equal:
                return lhsResolved == rhsResolved;

            default:
                return false;
        }
    }

    public String getOperatorString() {
        switch (operator) {
            case GreaterThan:
                return ">";
            case LessThan:
                return "<";
            case Equal:
                return "==";
            default:
                return "unknown";

        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indentationLevel) {
        return indentationLevel + lhs.toString() + " " + getOperatorString() + " " + rhs.toString();
    }
}
