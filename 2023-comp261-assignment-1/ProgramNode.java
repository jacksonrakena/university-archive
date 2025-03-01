/**
 * Interface for all nodes that can be executed,
 * including the top level program node
 */

interface ProgramNode extends Node {
    public void execute(Robot robot);

    public String toString(String indentationLevel);
}
