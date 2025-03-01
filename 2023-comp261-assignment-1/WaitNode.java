public class WaitNode implements ProgramNode{
    private final ExpressionNode<Integer> turns;
    public WaitNode(ExpressionNode<Integer> turns){
        this.turns = turns;
    }
    @Override
    public void execute(Robot robot) {
        int turnsToDo = turns.evaluate(robot);
        for (int i = 0; i < turnsToDo; i++) {
            robot.idleWait();
        }
    }
    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel + "Wait[" + this.turns + "]";
    }
}
