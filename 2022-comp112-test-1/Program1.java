// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2022T1, Online test
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;

import ecs100.UI;

/**
 * Question 6.1. Writing methods that use objects [10 marks]
 * 
 * Complete the animate() method so that it:
 * 
 * 1. Creates a sprite named "Thor", at position 100, facing right.
 * 2. Creates a second sprite named "Loki", at position 500, facing left.
 * 3. Calls a sequence of methods to do the following animation:
 * Thor must move right to the edge of the pit (two moves right) and jump over
 * the pit,
 * Thor announce "The god of thunder!"
 * Loki jumps over the pit and moves one left,
 * Loki announce "I want your power!", and attacks.
 * Thor then moves right and attacks.
 * 
 * NB: the sprites will not actually fall into the pits if they move over them,
 * but they need to jump over them for your answer to be correct.
 */
public class Program1 {

    /**
     * Animates two sprites
     */
    public void animate() {
        UI.clearGraphics();
        this.drawBackground();

        // 1
        Sprite thor = new Sprite("Thor", 100, true);

        // 2
        Sprite loki = new Sprite("Loki", 500, false);

        // 3a
        thor.move("right");
        thor.move("right");
        thor.jump();

        // 3b
        thor.announce("The god of thunder!");

        // 3c
        loki.jump();
        loki.move("left");

        // 3d
        loki.announce("I want your power!");
        loki.attack();

        // 3e
        thor.move("right");
        thor.attack();
    }

    /*********************************************
     * YOU CAN IGNORE EVERYTHING BELOW THIS LINE *
     *********************************************/
    public void setupGUI() {
        UI.initialise();
        UI.addButton("Run", this::animate);
        UI.setWindowSize(600, 500);
        UI.setDivider(0.0);
        drawBackground();
    }

    public void drawBackground() {
        // Water
        UI.setColor(new Color(53, 40, 200));
        UI.fillRect(0, 400, UI.getCanvasWidth(), UI.getCanvasHeight());

        // Dirt
        UI.setColor(new Color(107, 75, 20));
        UI.fillRect(0, 300, 240, UI.getCanvasHeight());
        UI.fillRect(270, 300, 180, UI.getCanvasHeight());
        UI.fillRect(470, 300, UI.getCanvasWidth() - 430, UI.getCanvasHeight());
        // Grass
        UI.setColor(new Color(25, 128, 61));
        UI.fillRect(0, 300, 240, 25);
        UI.fillRect(270, 300, 180, 25);
        UI.fillRect(470, 300, UI.getCanvasWidth() - 430, 25);

    }

    public static void main(String[] args) {
        new Program1().setupGUI();
    }
}
