// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 3
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import ecs100.UI;

/**
 * Program to create simple animated animal character using the
 * Animal class.
 */

public class PetShow {

    /**
     * animate creates two or several animals on the window.
     * Then animates them according to a fixed script by calling a series
     * of methods on the animals.
     * 
     * CORE
     */
    public void animate() {
        UI.clearGraphics();
        Animal gabeTheDog = new Animal("dog", "Gabe", 100, 50);
        gabeTheDog.goLeft(20);
        gabeTheDog.goRight(30);
        gabeTheDog.speak("bork");
        gabeTheDog.jump(10);

        Animal sylvester = new Animal("dinosaur", "Sylvester", 100, 400);
        sylvester.goLeft(40);
        sylvester.goRight(20);
        sylvester.goLeft(10);
        sylvester.goRight(50);
        sylvester.shout("squeak");
    }

    /**
     * threeAnimalsRoutine creates three animals on the window.
     * Then makes each animal do the same routine in turn.
     * You should define a routine method, and threeAnimalsRoutine
     * should call the routine method three times, to make
     * each of the three animals perform the routine in turn.
     * 
     * COMPLETION
     */
    public void threeAnimalsRoutine() {
        UI.clearGraphics();
        Animal tom = new Animal("bird", "Tom", 100, 50);
        Animal jerry = new Animal("grasshopper", "Jerry", 100, 200);
        Animal bob = new Animal("turtle", "Bob", 100, 350);
        routine(tom, "bird");
        routine(jerry, "grasshopper");
        routine(bob, "turtle");
    }

    /**
     * makes the animal character do a little routine
     */
    public void routine(Animal animal, String type) {
        animal.goLeft(40);
        animal.goRight(20);
        animal.jump(20);
        animal.goLeft(10);
        animal.goRight(50);
        animal.jump(20);
        animal.speak("Hello! I am a " + type + " that speaks English! Crazy, right?");
    }

    /**
     * Challenge: the animals perform separate routines
     */
    public void doChallenge() {
        UI.clearGraphics();
        Animal tom = new Animal("bird", "Tom", 100, 50);
        Animal jerry = new Animal("grasshopper", "Jerry", 100, 200);
        Animal bob = new Animal("turtle", "Bob", 100, 350);
        routine_jumpHurdles(tom);

        jerry.speak("I am fast.");
        routine_goFastBackAndForth(5, jerry);

        bob.speak("I am slow.");
        routine_spinAroundSmall(10, bob);
    }

    // This function makes an animal jump over a series of hurdles
    public void routine_jumpHurdles(Animal animal) {
        animal.goLeft(160);
        for (int i = 0; i < 160; i = i + 40) {
            animal.goRight(40);
            animal.jump(30);
        }
    }

    // This function makes an animal go back and forth by 'dist' units,
    // 'numberOfSpins' times
    public void routine_spinAround(int numberOfSpins, Animal animal, int dist) {
        for (int i = 0; i < numberOfSpins; i++) {
            animal.goLeft(dist);
            animal.goRight(dist);
        }
    }

    // This function makes an animal go back and forth by 80 units,
    // 'numberOfSpins' times
    public void routine_spinAroundSmall(int numberOfSpins, Animal animal) {
        routine_spinAround(numberOfSpins, animal, 80);
    }

    // This function makes an animal go back and forth by 20 units,
    // 'numberOfSpins' times
    public void routine_goFastBackAndForth(int numberOfTimes, Animal animal) {
        routine_spinAround(numberOfTimes, animal, 20);
    }

    /** Make buttons to let the user run the methods */
    public void setupGUI() {
        UI.initialise();
        UI.addButton("Clear", UI::clearGraphics);
        UI.addButton("Animate", this::animate);
        UI.addButton("Three", this::threeAnimalsRoutine);
        UI.addButton("Separate (Challenge)", this::doChallenge);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0); // Expand the graphics area
    }

    public static void main(String[] args) {
        PetShow ps = new PetShow();
        ps.setupGUI();
    }
}
