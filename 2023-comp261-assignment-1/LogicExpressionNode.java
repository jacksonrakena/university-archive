public class LogicExpressionNode extends BooleanExpressionNode {
    private final BooleanOperator operator;
    private final ExpressionNode<Boolean> lhs;
    private final ExpressionNode<Boolean> rhs;
    public LogicExpressionNode(BooleanOperator operator, ExpressionNode<Boolean> lhs, ExpressionNode<Boolean> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Boolean evaluate(Robot robot) {
        switch (this.operator) {
            case And:
                return lhs.evaluate(robot) && rhs.evaluate(robot);

            case Or:
                return lhs.evaluate(robot) || rhs.evaluate(robot);

            case Not:
                return !lhs.evaluate(robot);

            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        if (this.operator == BooleanOperator.Not) {
            return indentationLevel + "!" + this.lhs.toString();
        }
        String text = this.lhs.toString() + " " + this.operator.toString() + " " + this.rhs.toString();

        return indentationLevel + text;
    }
}
