import java.util.HashMap;
import java.util.LinkedList;

public class BlockNode implements ProgramNode {
    private final HashMap<String, Integer> variables = new HashMap<>();
    protected final LinkedList<ProgramNode> children = new LinkedList<>();
    public void addChildNode(ProgramNode child) {
        children.add(child);
    }
    @Override
    public void execute(Robot robot) {
        for (ProgramNode child : children) {
            child.execute(robot);
        }
    }

    private final BlockNode parent;

    public BlockNode(BlockNode parent) {
        this.parent = parent;
    }
    public void createVariable(String name) {
        this.variables.put(name,0);
    }

    public void setVariable(String name, int value) {
        if (variables.containsKey(name)) {
            variables.put(name, value);
            return;
        }
        BlockNode p = this.parent;
        while (p != null) {
            if (p.variables.containsKey(name)){
                p.variables.put(name, value);
                return;
            }
            p = p.parent;
        }
        variables.put(name, value);
    }

    public Integer resolveVariable(String name) {
        if (variables.containsKey(name)) return variables.get(name);
        BlockNode p = this.parent;
        while (p != null) {
            if (p.variables.containsKey(name)) return p.variables.get(name);
            p = p.parent;
        }
        return 0;
    }

    public boolean ensureVariableExists(String name) {
        if (variables.containsKey(name)) return true;
        BlockNode p = this.parent;
        while (p != null) {
            if (p.variables.containsKey(name)) return true;
            p = p.parent;
        }
        return false;
    }

    @Override
    public String toString(String indentationLevel) {
        StringBuilder text = new StringBuilder(" {" + System.lineSeparator());
        for (var child : children) {
            text.append(child.toString(indentationLevel + Parser.INDENTATION));
            text.append(System.lineSeparator());
        }
        text.append(indentationLevel).append("}");
        text.append(System.lineSeparator());
        return text.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }
}
