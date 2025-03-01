public class InfiniteLoopNode implements ProgramNode{
    private final ProgramNode block;
    public InfiniteLoopNode(ProgramNode block) {
        this.block = block;
    }
    @Override
    public void execute(Robot robot) {
        while (true) {
            block.execute(robot);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String indentationLevel) {
        return indentationLevel +  "Loop"+this.block.toString(indentationLevel);
    }
}
