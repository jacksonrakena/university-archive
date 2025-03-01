// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    public GTNode<ExpElem> storedExpression;

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> t = readExpr();
                if (t != null) {
                    storedExpression = t;
                    double value = evaluate(storedExpression);
                    UI.println(" -> " + value);
                }
            }
            catch (NoSuchElementException e) {
                UI.println("Missing a closing bracket.");
                UI.println(" -> " + Double.NaN);
            }
            catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Prints the last stored expression in infix-notation.
     */
    public void printExpression() {
        if (this.storedExpression == null) {
            UI.println("You need to evaluate an expression first.");
            return;
        }
        UI.println("Infix: " + parseToInfix(this.storedExpression));
    }

    /**
     * This string is printed if the calculator doesn't understand in expression,
     * when in infix mode.
     */
    public final String invalidInfixExpression = "[?]";


    /**
     * Parses the given expression to an infix-style string.
     */
    public String parseToInfix(GTNode<ExpElem> expr) {
        if (expr==null){
            return invalidInfixExpression;
        }
        switch (expr.getItem().operator) {
            case "#":
                return String.valueOf(expr.getItem().value);
            case "PI":
                return "PI";
            case "E":
                return "E";
            default:
                break;
        }
        StringBuilder buf = new StringBuilder();
        buf.append("(");

        if (expr.numberOfChildren() == 0) {
            UI.println("Expected operands for '" + expr.getItem().operator + "'");
            return invalidInfixExpression;
        }
        switch (expr.getItem().operator) {
            case "+":
                buf.append(expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining("+")));
                break;
            case "*":
                buf.append(expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining("*")));
                break;
            case "-":
                buf.append(expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining("-")));
                break;
            case "/":
                buf.append(expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining("/")));
                break;
            case "^":
                if (expr.numberOfChildren() != 2) {
                    UI.println("Cannot use the ^ operator with more than two arguments.");
                    return invalidInfixExpression;
                }
                buf.append(expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining("/")));
                break;
            case "sqrt":
                buf.append("sqrt " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "log":
                double base = 10;
                if (expr.numberOfChildren() == 2) base = evaluate(expr.getChild(1));
                else if (expr.numberOfChildren() != 1) {
                    UI.println("'log' operator requires exactly 1 or 2 operands.");
                    return invalidInfixExpression;
                }
                buf.append("log" +base+ " " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "ln":
                if (expr.numberOfChildren() != 1) {
                    UI.println("'ln' operator requires exactly 1 operand.");
                    return invalidInfixExpression;
                }
                buf.append("ln " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "sin":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return invalidInfixExpression;
                }
                buf.append("sin " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "cos":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return invalidInfixExpression;
                }
                buf.append("cos " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "tan":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return invalidInfixExpression;
                }
                buf.append("tan " + parseToInfix(expr.getChildren().get(0)));
                break;
            case "dist":
                if (expr.numberOfChildren() == 4) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double x2 = evaluate(expr.getChild(2));
                    double y2 = evaluate(expr.getChild(3));
                    buf.append(String.format("dist(%s,%s, %s,%s)", x1, y1, x2, y2));
                } else if (expr.numberOfChildren() == 6) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double z1 = evaluate(expr.getChild(2));
                    double x2 = evaluate(expr.getChild(3));
                    double y2 = evaluate(expr.getChild(4));
                    double z2 = evaluate(expr.getChild(5));
                    buf.append(String.format("dist(%s,%s,%s, %s,%s,%s)", x1, y1, z1, x2, y2, z2));
                } else {
                    UI.println("Cannot perform operation 'dist' on " + expr.numberOfChildren() + " operands.");
                    return invalidInfixExpression;
                }
                break;
            case "avg":
                buf.append("avg(" + expr.getChildren().stream().map(this::parseToInfix).collect(Collectors.joining(", ")) + ")");
                break;
            default:
                UI.println("Unknown operator " + expr.getItem().operator);
                return invalidInfixExpression;
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }
        switch (expr.getItem().operator) {
            case "#":
                return expr.getItem().value;
            case "PI":
                return Math.PI;
            case "E":
                return Math.E;
            default:
                break;
        }

        if (expr.numberOfChildren() == 0) {
            UI.println("Expected operands for '" + expr.getItem().operator + "'");
            return Double.NaN;
        }

        double total = evaluate(expr.getChild(0));
        if (Double.isNaN(total)) return Double.NaN;
        switch (expr.getItem().operator) {
            case "+":
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    total += evaluate(expr.getChild(i));
                }
                break;
            case "*":
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    total *= evaluate(expr.getChild(i));
                }
                break;
            case "-":
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    total -= evaluate(expr.getChild(i));
                }
                break;
            case "/":
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    total = total / evaluate(expr.getChild(i));
                }
                break;
            case "^":
                if (expr.numberOfChildren() != 2) {
                    UI.println("Cannot use the ^ operator with more than two arguments.");
                    return Double.NaN;
                }
                total = Math.pow(total,evaluate(expr.getChild(1)));
                break;
            case "sqrt":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Cannot use the sqrt operator with more than one argument.");
                    return Double.NaN;
                }
                total = Math.sqrt(total);
                break;
            case "log":
                double base = 10;
                if (expr.numberOfChildren() == 2) base = evaluate(expr.getChild(1));
                else if (expr.numberOfChildren() != 1) {
                    UI.println("'log' operator requires exactly 1 or 2 operands.");
                    return Double.NaN;
                }
                total = Math.log(total) / Math.log(base);
                break;
            case "ln":
                if (expr.numberOfChildren() != 1) {
                    UI.println("'ln' operator requires exactly 1 operand.");
                    return Double.NaN;
                }
                total = Math.log(total);
                break;
            case "sin":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return Double.NaN;
                }
                total = Math.sin(total);
                break;
            case "cos":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return Double.NaN;
                }
                total = Math.cos(total);
                break;
            case "tan":
                if (expr.numberOfChildren() != 1) {
                    UI.println("Trigonometric operators require exactly 1 operand.");
                    return Double.NaN;
                }
                total = Math.tan(total);
                break;
            case "dist":
                if (expr.numberOfChildren() == 4) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double x2 = evaluate(expr.getChild(2));
                    double y2 = evaluate(expr.getChild(3));
                    total = Math.hypot(x1-x2, y1-y2);
                } else if (expr.numberOfChildren() == 6) {
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double z1 = evaluate(expr.getChild(2));
                    double x2 = evaluate(expr.getChild(3));
                    double y2 = evaluate(expr.getChild(4));
                    double z2 = evaluate(expr.getChild(5));
                    total = Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2) + Math.pow(z1-z2, 2));
                } else {
                    UI.println("Cannot perform operation 'dist' on " + expr.numberOfChildren() + " operands.");
                    return Double.NaN;
                }
                break;
            case "avg":
                for (int i = 1; i < expr.numberOfChildren(); i++) {
                    total += evaluate(expr.getChild(i));
                }
                total = total / expr.numberOfChildren();
                break;
            default:
                UI.println("Unknown operator " + expr.getItem().operator);
                return Double.NaN;
        }
        return total;
    }

    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr (type 'infix' to see the last expression as infix):");
        if (expr.equals("infix")) {
            printExpression();
            return null;
        }
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();
           // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);
            }
            if (node.numberOfChildren() == 0) {
                UI.println("Found empty brackets.");
                return null;
            }
            sc.next();

            // read and throw away the closing ')'
            return node;
        }
        else {
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}

