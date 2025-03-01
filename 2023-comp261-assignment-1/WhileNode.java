public class WhileNode implements ProgramNode{
    private final ExpressionNode<Boolean> condition;
    private final ProgramNode block;

    public WhileNode(ExpressionNode<Boolean> condition, ProgramNode block) {
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void execute(Robot robot) {
        while (condition.evaluate(robot)) {
            block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }
    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "While(" +this.condition +")" + this.block.toString(indentationLevel);
    }
}
