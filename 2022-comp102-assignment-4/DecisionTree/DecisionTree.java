// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 4
 * Name:
 * Username:
 * ID:
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");

        List<Integer> hello = new ArrayList<>();
        hello.add(1);
        hello.add(2);
        hello.add(3);
        for (int i = 0; i < getSize(hello); i++) {
            UI.println("I=" + i);
        }
    }

    public static <T>  int getSize(List<T> l) {
        UI.println("Getting size");
        return l.size();
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        UI.clearText();
        printNode(theTree, 1);
    }
    String spacer = "  ";

    public void printNode(DTNode node, int level) {
        if (node.isAnswer()) UI.println(node.getText());
        else {
            UI.println(node.getText() + "?");

            for (Map.Entry<String, DTNode> child : node.getChildren().entrySet()) {
                UI.print(spacer.repeat(level)+child.getKey()+":");
                printNode(child.getValue(), level+1);
            }
        }
    }

    public DTNode resolveQuestionNode(DTNode node) {
        while (!node.isAnswer()) {
            String response = UI.askString("\"" + node.getText() + "\"? (" + node.getChildren().values() + ")");
            for (Map.Entry<String, DTNode> child : node.getChildren().entrySet()) {
                if (child.getKey().equalsIgnoreCase(response)) {
                    node = child.getValue();
                }
            }
        }
        return node;
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        UI.println("The answer is: " + resolveQuestionNode(theTree).getText());
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        DTNode base = resolveQuestionNode(theTree);
        boolean correct = UI.askBoolean("I think I know. Is it a " + base.getText() + "? ");
        if (!correct) {
            String answer = UI.askString("OK, what should the answer be? ");
            UI.println("Oh. I can't distinguish a " + base.getText() + " from a " + answer + ".");
            UI.println("Tell me something that's true for a " + answer + " but not for a " + base.getText() + ":");
            String property = UI.askString("Property: ");
//            base.setChildren(new DTNode(answer), new DTNode(base.getText()));
//            base.setText(property);
        }
    }

    public void saveTree() {
        String path = UIFileChooser.save("Where to save the decision tree? ");
        StringBuilder builder = new StringBuilder();
        appendFormattedNode(theTree, builder);
        try {
            Files.writeString(Path.of(path), builder.toString());
        } catch (IOException e) {
            UI.println("Failed to save:");
            UI.println(e);
        }
    }

    public void appendFormattedNode(DTNode node, StringBuilder builder) {
        if (node.isAnswer()) builder.append("Answer: " + node.getText() + System.lineSeparator());
        else {
            builder.append("Question: " + node.getText() + System.lineSeparator());
            appendFormattedNode(node.getYes(), builder);
            appendFormattedNode(node.getNo(), builder);
        }
    }

    public int findDepth(DTNode node) {
//        if (node == null || node.isAnswer()) return 0;
//        int yes = findDepth(node.getYes());
//        int no = findDepth(node.getNo());
//        if (yes > no) return yes+1;
//        return no+1;
    }

    public void drawTree() {
//        int depthOfTree = 1 + findDepth(theTree);
//        UI.println(findDepth(theTree));
//
//        float height = 400;

    }

    public void drawNode(DTNode node, int x, int y) {
        int depthOfNode = findDepth(node);
    }


    // You will need to define methods for the Completion and Challenge parts.

    // Written for you

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }



}
