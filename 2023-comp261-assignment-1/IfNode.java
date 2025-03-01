import java.util.List;

public class IfNode implements ProgramNode{
    private final BooleanExpressionNode condition;
    private final ProgramNode block;

    private final ProgramNode elseBranch;

    private final List<IfNode> elifBranches;

    private final boolean isElif;

    public IfNode(BooleanExpressionNode condition, ProgramNode block, ProgramNode elseBranch, List<IfNode> elifBranches, boolean isElif) {
        this.condition = condition;
        this.block = block;
        this.elseBranch = elseBranch;
        this.elifBranches = elifBranches;
        this.isElif = isElif;
    }

    @Override
    public void execute(Robot robot) {
        if (condition.evaluate(robot)) {
            block.execute(robot);
        }
        else {
            if (elseBranch != null) {
                elseBranch.execute(robot);
            }
            for (var elif : elifBranches) {
                elif.execute(robot);
            }
        }
    }
    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        String name = isElif ? "Elif" : "If";
        StringBuilder text= new StringBuilder(indentationLevel + name+"(" + this.condition + ")" + this.block.toString(indentationLevel));
        for (var branch: elifBranches) {
            text.append(branch.toString(indentationLevel));
        }
        if (elseBranch != null) text.append(indentationLevel).append("Else").append(this.elseBranch.toString(indentationLevel));
        return text.toString();
    }
}
