import java.util.*;
import java.util.regex.*;
public class ParsingChallenge extends Parser {
    @Override
    public void variableAccessHook(BlockNode node, Scanner s, String name) {
        if (!node.ensureVariableExists(name)) {
            // Block assignments to undeclared variables
            fail("undeclared variable '" + name + "'", s);
        }
    }

    @Override
    public void variableAssignmentHook(BlockNode node, Scanner s, String name) {
        if (!node.ensureVariableExists(name)) {
            // Ensure we're not trying to express an undeclared variable
            fail("undeclared variable '" + name + "'", s);
        }
    }

    @Override
    public void registerVariablesHook(BlockNode node, Scanner s) {
        if (s.hasNext("vars")) {
            // Variable declarations
            s.next();
            boolean read = true;
            while (read) {
                if (s.hasNext(";")) {
                    s.next();
                    read = false;
                }
                else if (s.hasNext(VARIABLE_NAME)) {
                    String varName = s.next();
                    node.createVariable(varName);
                } else if (s.hasNext(",")) {
                    s.next();
                }
                else {
                    fail("illegal variable name '" + s.next() + "'", s);
                    read = false;
                }
            }
        }
    }
}
