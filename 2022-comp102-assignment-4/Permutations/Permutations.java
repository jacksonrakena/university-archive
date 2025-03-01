// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;

/** 
 *  Compute all permutations of a list of Strings
 *
 *  You only have to write one method - the extendPermutations(...) method
 *  which does the recursive search.  
 */
public class Permutations {

    /**
     * Constructs a list of all permutations of the given items
     * by calling a recursive method, passing in a set of the items to permute
     * and an empty list to build up.
     * Prints the total number of permutations in the message window (with
     *  UI.printMessage(...);
     */
    public List<List<String>> findPermutations(Set<String> items){
        Set<String> copyOfItems = new HashSet<String>(items);   // a copy of the set of items that can be modified
        List<List<String>> ans = new ArrayList<List<String>>(); // where we will collect the answer
        //suggested approach:
        extendPermutation(copyOfItems, new Stack<String>(), ans);   

        return ans;
    }

    /**
     * Recursive method to build all permutations possible by adding the
     *  remaining items on to the end of the permutation built up so far 
     * If there are no remaining items, then permutationSoFar is complete,
     *   => add a copy of the permutation to allPermutations.
     * Otherwise,
     *  for each of the remaining items,
     *     extend the permutationSoFar with the item, and do a recursive call to extend it more:
     *     - remove the item from remaining items and
     *     - push it onto the permutation so far,
     *     - do the recursive call,
     *     - pop the item from the permutation so far and
     *     - put it back into the remaining items.
     *
     * So that you don't run out of memory, only add the first 10000 permutations to the allPermutations.
     */
    public void extendPermutation(Set<String> remainingItems, Stack<String> permutationSoFar, List<List<String>> allPermutations){
        if (allPermutations.size() > 10000) return;
        if (remainingItems.size() == 0) {
            allPermutations.add(new ArrayList<>(permutationSoFar));
            return;
        }
        for (String item : remainingItems) {
            Set<String> d = new HashSet<>(remainingItems);
            d.remove(item);
             permutationSoFar.push(item);
             extendPermutation(d, permutationSoFar, allPermutations);
             permutationSoFar.pop();
        }
    }

    //===================================================
    // User Interface code

    /**
     * Setup GUI
     * Buttons to run permutations on either letters or words
     */
    public void setupGUI(){
        UI.addButton("A B C D E", ()->{printAll(findPermutations(Set.of("A","B","C","D","E")));});
        UI.addTextField("Letters", (String v)->{printAll(findPermutations(makeSetOfLetters(v)));});
        UI.addTextField("Words", (String v)->{printAll(findPermutations(makeSetOfWords(v)));});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    public void printAll(List<List<String>> permutations){
        UI.clearText();
        for (List<String> permutation : permutations) {
            for (String str : permutation) {
                UI.print(str + " ");
            }
            UI.println();
        }
        UI.println("----------------------");
        UI.printf("%d items:\n", permutations.get(0).size());
        UI.printf("%,d permutations:\n", permutations.size());
        UI.println("----------------------");
    }

    /**
     * Makes a set of strings, one string for each character in the argument
     */
    public Set<String> makeSetOfLetters(String str){
        Set<String> ans = new HashSet<String>();
        for (int i=0; i<str.length(); i++){
            if (str.charAt(i)!=' '){
                ans.add(""+str.charAt(i));
            }
        }
        return Collections.unmodifiableSet(ans);
    }

    /**
     * Makes a set of strings, one string for each word in the argument
     */
    public Set<String> makeSetOfWords(String str){
        Set<String> ans = new HashSet<String>();
        for (String v : str.split(" ")){ans.add(v);}
        return Collections.unmodifiableSet(ans);
    }
    
    // Main
    public static void main(String[] arguments) {
        Permutations p = new Permutations();
        p.setupGUI();
    }
}
