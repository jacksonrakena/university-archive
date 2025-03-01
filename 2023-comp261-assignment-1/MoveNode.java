public class MoveNode implements ProgramNode{
    private final ExpressionNode<Integer> steps;
    public MoveNode(ExpressionNode<Integer> steps){
        this.steps = steps;
    }
    @Override
    public void execute(Robot robot) {
        int stepsToMove = steps.evaluate(robot);
        for (int i = 0; i < stepsToMove; i++) {
            robot.move();
        }
    }
    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "Move["+this.steps+"]";
    }
}
