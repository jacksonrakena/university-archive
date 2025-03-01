import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {
    public static final String INDENTATION = "    ";
    // Useful Patterns

    static final Pattern VARIABLE_NAME = Pattern.compile("\\$[A-Za-z][A-Za-z0-9]*");
    static final Pattern SEMICOLON = Pattern.compile(";");
    static final Pattern COMMA = Pattern.compile(",");
    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0"); 
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");

    //----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the node
        return parseCodeStatements(s, null);
    }

    /**
     * Attempts to parse a code block, by first checking for
     * braces and then calling {@link Parser#parseCodeStatements}.
     *
     * This method expects proper braces around the code block.
     */
    ProgramNode parseBlockWithBraces(Scanner s, BlockNode parent) {
        require(OPENBRACE, "Expected an opening bracket", s);
        var node = parseCodeStatements(s, parent);
        require(CLOSEBRACE, "Expected a closing bracket", s);
        return node;
    }

    /**
     * Attempts to parse a list of code statements.
     * Will return a BlockNode containing all valid statements.
     *
     * Will fail if an unknown token is detected, and will
     * propagate fails from child nodes.
     */
    BlockNode parseCodeStatements(Scanner s, BlockNode parent) {
        var node = new BlockNode(parent);
        boolean loop = true;
        this.registerVariablesHook(node, s);
        while (loop) {
            if (!s.hasNext()) {
                loop = false;
                continue;
            }
            var expectTerminal = true;
            if (s.hasNext("move")) {
                s.next();

                GenericExpressionNode moveSteps = new ConstantIntegerNode(1);
                // Check for move(INT)
                if (checkFor(OPENPAREN, s)) {
                    moveSteps = parseGenericExpression(node, s);
                    requireClosedParen("move", s);
                }
                if (!(moveSteps instanceof IntegerExpressionNode)) {
                    fail("Expected an integer expression for move() arguments", s);
                    return null;
                }
                node.addChildNode(new MoveNode((IntegerExpressionNode) moveSteps));
            }
            else if (s.hasNext("wait")) {
                s.next();
                GenericExpressionNode waitSteps = new ConstantIntegerNode(1);
                // Check for wait(NumberExpression)
                if (checkFor(OPENPAREN, s)) {
                    waitSteps = parseGenericExpression(node, s);
                    if (!(waitSteps instanceof IntegerExpressionNode)) {
                        fail("Expected an integer expression for wait() arguments", s);
                        return null;
                    }
                    requireClosedParen("wait", s);
                }
                node.addChildNode(new WaitNode((IntegerExpressionNode) waitSteps));
            }
            else if (s.hasNext("turnL")) {
                s.next();
                node.addChildNode(new TurnLNode());
            }
            else if (s.hasNext("turnR")) {
                s.next();
                node.addChildNode(new TurnRNode());
            }
            else if (s.hasNext("takeFuel")) {
                s.next();
                node.addChildNode(new TakeFuelNode());
            }
            else if (s.hasNext("turnAround")) {
                s.next();
                node.addChildNode(new TurnAroundNode());
            }
            else if (s.hasNext("loop")) {
                s.next();
                node.addChildNode(new InfiniteLoopNode(parseBlockWithBraces(s, node)));
                expectTerminal = false;
            }
            else if (s.hasNext("while")) {
                s.next();
                node.addChildNode(parseWhile(node, s));
                expectTerminal = false;
            }
            else if (s.hasNext("if")) {
                s.next();
                node.addChildNode(parseIf(node, s));
                expectTerminal = false;
            }
            else if(s.hasNext("shieldOn")) {
                s.next();
                node.addChildNode(new SetShieldNode(true));
            }
            else if (s.hasNext("shieldOff")) {
                s.next();
                node.addChildNode(new SetShieldNode(false));
            }
            else if (s.hasNext(CLOSEBRACE)) {
                if (node.children.size() == 0) fail("Expected one or more code statements in block.", s);
                return node;
            }
            else if (s.hasNext(VARIABLE_NAME)) {
                // Probably a variable assignment
                String name = s.next();
                require("=", "Expected an equals sign after a variable name", s);
                GenericExpressionNode expr = parseGenericExpression(node, s);
                if (!(expr instanceof IntegerExpressionNode e)) {
                    fail("Expected an integer expression for variable assignment", s);
                    return null;
                }
                this.variableAssignmentHook(node, s, name);
                node.addChildNode(new VariableAssignmentNode(node, name, e));
            }
            else {
                fail("Unexpected token: " + s.next(), s);
            }
            if (expectTerminal) requireTerminal("method",s);
        }
        if (node.children.size() == 0) fail("Expected one or more code statements in block.", s);
        return node;
    }

    /**
     * Parses a WHILE statement.
     *
     * Returns a WHILE node representing the boolean conditional
     * and the block node to execute in the loop.
     */
    ProgramNode parseWhile(BlockNode parent, Scanner s) {
        requireOpenParen("while", s);
        var cond = parseGenericExpression(parent, s);

        var booleanExpr = (BooleanExpressionNode) cond;
        if (booleanExpr == null) {
            fail("Expected a boolean expression inside 'while' loop",s);
            return null;
        }
        requireClosedParen("while", s);
        var block = parseBlockWithBraces(s, parent);
        return new WhileNode(booleanExpr, block);
    }

    /**
     * Parses an IF statement.
     * Will attempt to detect else and elif branches,
     * and add them to the node.
     *
     * Returns a node representing the IF and its branches.
     */
    IfNode parseIf(BlockNode parent, Scanner s) {
        requireOpenParen("if", s);
        if (!(parseGenericExpression(parent, s) instanceof BooleanExpressionNode cond)) {
            fail("Expected a boolean expression for if statement", s);
            return null;
        }
        requireClosedParen("if", s);
        var block = parseBlockWithBraces(s, parent);
        ProgramNode elseBranch = null;
        var elifBranches = new ArrayList<IfNode>();
        if (checkFor("else", s)) {
            // Else block detected
            elseBranch = parseBlockWithBraces(s, parent);
        }
        while (checkFor("elif", s)) {
            var elifBranch = parseElifNode(parent, s);
            elifBranches.add(elifBranch);
        }
        if (checkFor("else", s)) {
            // Else block detected
            elseBranch = parseBlockWithBraces(s, parent);
        }
        return new IfNode(cond, block, elseBranch, elifBranches, false);
    }

    IfNode parseElifNode(BlockNode parent, Scanner s) {
        requireOpenParen("if", s);
        if (!(parseGenericExpression(parent, s) instanceof BooleanExpressionNode cond)) {
            fail("Expected a boolean expression for elif statement", s);
            return null;
        }
        requireClosedParen("if", s);
        var block = parseBlockWithBraces(s, parent);
        return new IfNode(cond, block, null, List.of(), true);
    }

    /**
     * Attempts to parse an expression.
     *
     * According to the grammar rules, an expression can be any of the following:
     *
     * Boolean expressions (ExpressionNode<Boolean>)
     * - Boolean logic infix (left && right, left || right, !left)
     * - Boolean logic prefix (and(left,right), or(left,right), not(left))
     *
     * - Comparative infix (left > right, left < right, left == right)
     * - Comparitive prefix (gt(left, right), lt(left, right), eq(left, right))
     *
     * Integer expressions (ExpressionNode<Integer>)
     * - Arithmetic infix (left + right, left - right, left / right, left * right)
     * - Arithmetic prefix (add(left,right), sub(left,right), div(left,right), mul(left,right))
     *
     * - Variable literal ($fortnite)
     * - Sensor literal (oppLR)
     * - Sensor literal with argument (barrelLR(..integer expression..))
     * - Number literal (0)
     */
    GenericExpressionNode parseGenericExpression(BlockNode parent, Scanner s) {
        // Try parse a boolean logic prefix expression
        if (s.hasNext("and") || s.hasNext("or") || s.hasNext("not")) {
            // Resolving BooleanLogicNode (ValueNode<Boolean>)
            String logicalOperand = s.next();
            BooleanOperator operand;
            switch (logicalOperand) {
                case "and" -> operand = BooleanOperator.And;
                case "or" -> operand = BooleanOperator.Or;
                case "not" -> operand = BooleanOperator.Not;
                default -> {
                    fail("Unknown boolean operator '" + logicalOperand + "'", s);
                    return null;
                }
            }
            requireOpenParen(logicalOperand, s);
            if (!(parseGenericExpression(parent, s) instanceof BooleanExpressionNode lhs)) {
                fail("Expected a boolean expression on the left-hand side of '" + logicalOperand + "'",s);
                return null;
            }
            BooleanExpressionNode rhs = null;
            if (s.hasNext(",")) {
                s.next();
                var generic = parseGenericExpression(parent, s);
                if (!(generic instanceof BooleanExpressionNode)) {
                    fail("Expected a boolean expression on the left-hand side of '" + logicalOperand + "'",s);
                    return null;
                }
                rhs = (BooleanExpressionNode) generic;
            }

            if (operand != BooleanOperator.Not && rhs == null) {
                fail("Need a right-hand side expression to use operand '"+logicalOperand+"'",s);
                return null;
            }
            if (operand == BooleanOperator.Not && rhs != null) {
                fail("Illegal right-hand side expression for operand '"+logicalOperand+"'",s );
            }

            requireClosedParen(logicalOperand, s);
            return new LogicExpressionNode(operand, lhs, rhs);
        }


        // Try parse a comparative prefix boolean
        boolean gt = s.hasNext("gt");
        boolean lt = s.hasNext("lt");
        boolean eq = s.hasNext("eq");
        if (gt || lt || eq) {
            // Relational operator detected
            var relop = parseRelationalOperator(s);
            requireOpenParen(relop.toString(), s);
            // parse lhs
            if (!(parseGenericExpression(parent, s) instanceof IntegerExpressionNode lhs)) {
                fail("Expected an integer expression for the left-hand side of a relational operation.", s);
                return null;
            }
            require(",", "Expected a right-hand side in a relational operator expression", s);
            if (!(parseGenericExpression(parent, s) instanceof IntegerExpressionNode rhs)) {
                fail("Expected an integer expression for the right-hand side of a relational operation.", s);
                return null;
            }
            requireClosedParen(relop.toString(), s);
            return new ComparativeExpressionNode(relop, lhs, rhs);
        }

        String lhsToken = s.next();

        // Try parse an arithmetic prefix integer
        var operator = EnumUtilities.parseArithmeticOperatorOrNull(lhsToken);
        if (operator != null) {
            requireOpenParen(operator.toString(), s);
            if (!(parseGenericExpression(parent, s) instanceof IntegerExpressionNode lhs)) {
                fail("Expected an integer expression in an arithmetic operation.", s);
                return null;
            }
            require(",", "Expected a right-hand side in an arithmetic operation", s);
            if (!(parseGenericExpression(parent, s) instanceof IntegerExpressionNode rhs)) {
                fail("Expected an integer expression in an arithmetic operation.", s);
                return null;
            }
            requireClosedParen(operator.toString(), s);
            return new ArithmeticExpressionNode(operator, lhs, rhs);
        }

        GenericExpressionNode lhs = null;
        BooleanOperator bool = null;
        RelationalOperator rel = null;
        ArithmeticOperator arith = null;

        if (OPENPAREN.matcher(lhsToken).matches()) {
            // Another bracket
            lhs = parseGenericExpression(parent, s);
            requireClosedParen("expression", s);
        }
        else if (VARIABLE_NAME.matcher(lhsToken).matches()) {
            this.variableAccessHook(parent, s, lhsToken);
            // Variable match
            lhs = new VariableLiteralNode(parent, lhsToken);
        } else if (NUMPAT.matcher(lhsToken).matches()) {
            // Number literal
            lhs = new ConstantIntegerNode(Integer.parseInt(lhsToken));
        }
        else {
            var sensor = EnumUtilities.parseSensorNameOrNull(lhsToken);
            if (sensor != null) {
                // Sensor literal
                if (checkFor(OPENPAREN, s)) {
                    if (!(parseGenericExpression(parent, s) instanceof IntegerExpressionNode rhs)) {
                        fail("Expected an integer expression for a sensor literal argument.", s);
                        return null;
                    }
                    requireClosedParen(sensor.name(), s);
                    lhs = new SensorValueNode(sensor, rhs);
                } else lhs = new SensorValueNode(sensor, null);
            }
        }

        // Try parse operator
        if (s.hasNext(">")) {
            rel = RelationalOperator.GreaterThan;
        } else if (s.hasNext("<")) {
            rel = RelationalOperator.LessThan;
        } else if (s.hasNext("==")) {
            rel = RelationalOperator.Equal;
        } else if (s.hasNext("&&")) {
            bool = BooleanOperator.And;
        } else if (s.hasNext("\\||")) {
            bool = BooleanOperator.Or;
        } else if (s.hasNext("\\+")) {
            arith = ArithmeticOperator.Add;
        } else if (s.hasNext("-")) {
            arith = ArithmeticOperator.Subtract;
        } else if (s.hasNext("/")) {
            arith = ArithmeticOperator.Divide;
        } else if (s.hasNext("\\*")) {
            arith = ArithmeticOperator.Multiply;
        } else {
            return lhs;
        }
        s.next();
        GenericExpressionNode rhs = parseGenericExpression(parent, s);
        if (rel != null) {
            return new ComparativeExpressionNode(rel, (IntegerExpressionNode)lhs, (IntegerExpressionNode)rhs);
        } else if (bool != null) {
            return new LogicExpressionNode(bool, (BooleanExpressionNode) lhs, (BooleanExpressionNode) rhs);
        } else if (arith != null) {
            return new ArithmeticExpressionNode(arith, (IntegerExpressionNode)lhs, (IntegerExpressionNode)rhs);
        }
        return null;
    }

    /**
     * Parses a relational operator, such as `gt`, `lt`, or `eq`.
     * Returns a relational operator enum representing the operation.
     * Will fail if the scanner has reached the end,
     * or the input text is not as expected.
     */
    RelationalOperator parseRelationalOperator(Scanner s) {
        if (!s.hasNext()) {
            fail("Expected a relational operator", s);
            return null;
        }
        String raw = s.next();
        switch (raw) {
            case "gt":
                return RelationalOperator.GreaterThan;
            case "lt":
                return RelationalOperator.LessThan;
            case "eq":
                return RelationalOperator.Equal;
            default:
                fail("Unknown relational operator " + raw, s);
                return null;
        }
    }


    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    static void warn(String message, Scanner s) {
        System.out.println("warn: " + message);
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String requireTerminal(String keyword, Scanner s) {
        return require(SEMICOLON, "Expected a semicolon to terminate '" + keyword + "' statement", s);
    }

    static String requireOpenParen(String keyword, Scanner s) {
        return require(OPENPAREN, "Expected an open parenthesis after keyword '" + keyword + "'", s);
    }

    static String requireClosedParen(String keyword, Scanner s) {
        return require(CLOSEPAREN, "Expected a closed parenthesis to end '" + keyword + "'", s);
    }

    static String requireOpenBracket(String keyword, Scanner s) {
        return require(OPENBRACE, "Expected an open bracket after keyword '" + keyword + "'", s);
    }

    static String requireClosedBracket(String keyword, Scanner s) {
        return require(CLOSEBRACE, "Expected a closed bracket to end '" + keyword + "' block.", s);
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;} 
        return false;
    }

    /**
     * The following hooks allow the challenge (Stage 4) parser
     * to override and insert behaviour into the parser.
     *
     * This is preferrable to copying the parser code, as changes to the Core parser would require the same changes
     * to be made to the Challenge parser copy which may lead to errors.
     */

    // Called when a variable is about to be used in an expression node.
    // Challenge code checks that the variable exists in scope.
    public void variableAccessHook(BlockNode node, Scanner s, String name) {}

    // Called when a VariableAssignmentNode is about to be created.
    // Checks that the target variable exists and is in scope.
    public void variableAssignmentHook(BlockNode node, Scanner s, String name) {}

    // Called at the start of each parsing block.
    // Used to register variables inside this scope.

    public void registerVariablesHook(BlockNode node, Scanner s) { }
}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//

