public class ArithmeticExpressionNode extends IntegerExpressionNode {
    private final ArithmeticOperator operator;
    private final ExpressionNode<Integer> lhs;
    private final ExpressionNode<Integer> rhs;

    public ArithmeticExpressionNode(ArithmeticOperator operator, ExpressionNode<Integer> lhs, ExpressionNode<Integer> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Integer evaluate(Robot robot) {
        int lhsResolved = this.lhs.evaluate(robot);
        int rhsResolved = this.rhs.evaluate(robot);
        switch (this.operator) {
            case Add:
                return lhsResolved + rhsResolved;
            case Subtract:
                return lhsResolved-rhsResolved;
            case Multiply:
                return lhsResolved*rhsResolved;
            case Divide:
                return lhsResolved/rhsResolved;
        }
        return null;
    }
    public String toString(String indentationLevel) {
        return indentationLevel + this.toString();
    }

    @Override
    public String toString() {
        String operatorText = this.operator.toString();
        switch (this.operator) {
            case Add -> operatorText = "+";
            case Subtract -> operatorText = "-";
            case Multiply -> operatorText = "*";
            case Divide -> operatorText = "/";
        }
        return "(" + this.lhs + " " + operatorText + " " + this.rhs + ")";
    }
}
