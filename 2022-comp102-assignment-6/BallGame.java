// This program is copyright VUW.

// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 6
 * Name: Jackson Rakena
 * Username: rakenajack
 * ID: 300609159
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ecs100.UI;

/**
 * Contains the code for the ball game simulation.
 */
public class BallGame {
    public static final double GROUND = 450; // Position of the ground
    public static final double LAUNCHER_X = 20; // The initial position of the ball being launched
    public static final double LAUNCHER_HEIGHT = 20; // The initial height of the ball being launched
    public static final double RIGHT_END = 600; // End of the map on the X axis
    public static final double SHELF_X = 400; // Shelf position for Core
    public static final double MAX_SPEED = 14; // Maximum ball velocity
    public static final double MSEC_PER_FRAME = 30; // The number of milliseconds between frames
    public static final double MAX_TIME_MSEC = 15000; // Total number of milliseconds allowed before the ball resets
    public static boolean run = false;
    public static long seed = -1;

    public static final double DIAM = Ball.DIAM;

    private PlayerTrajectoryIndicator indicator = new PlayerTrajectoryIndicator(); // Singleton instance

    private Ball ball; // the player's ball

    /** Setup the mouse listener and the buttons */

    public void setupGUI() {
        UI.setMouseMotionListener(this::handleMouseMotion);
        UI.addButton("Core (one target)", this::runGameCore);
        UI.addButton("Challenge (3-10 targets)", this::runGameChallengeCompletion);
        UI.addButton("Set seed", this::openSetSeedMenu);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(650, 500);
        UI.setDivider(0);
    }

    // Custom seeds
    public void openSetSeedMenu() {
        JDialog menu = new JDialog(UI.getFrame(), "Seed");
        JPanel panel = new JPanel();
        JTextField field = new JTextField(10);
        field.setSize(50, 10);
        JButton set = new JButton("Set");
        set.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu.setVisible(false);
                seed = Long.parseLong(field.getText());
            }
        });
        panel.add(new JLabel("(Adding a seed is optional, but allows you to compete against other players)"));
        panel.add(new JLabel("Please enter a numerical seed:"));
        panel.add(field);
        panel.add(set);
        menu.add(panel);
        menu.setVisible(true);
        menu.setSize(400, 400);

        UI.getFrame().validate();
    }

    // Handle mouse movement for the trajectory indicator
    public void handleMouseMotion(String action, double x, double y) {
        if (action == "moved") {
            indicator.update(x, y);
        } else if (action == "released") {
            launch(action, x, y);
        }
    }

    /**
     * Main loop for the Core version of the game
     * It creates a ball (to be launched) and a target ball (on a shelf)
     * It has a loop that repeatedly
     * - Makes a new ball if the old one has gone off the right end.
     * - Makes the ball and the target ball take one step
     * (unless they are still on the launcher or shelf)
     * - Checks whether the ball is hitting the target
     * - redraws the state of the game
     * The loop stops when the target has gone off the right end and the ball is on
     * the launcher.
     */
    public void runGameCore() {
        this.run = false;
        UI.printMessage("Click Mouse to launch the ball");
        this.ball = new Ball(LAUNCHER_X, LAUNCHER_HEIGHT);

        // Initialize player ball and game world
        double shelfHeight = 50 + Math.random() * 400;
        Ball target = new Ball(SHELF_X, shelfHeight);
        ArrayList<GameObject> gameElements = new ArrayList<GameObject>(Arrays.asList(
                new World(), ball, target, new PlayerLauncher(),
                new Shelf(SHELF_X - DIAM / 2, shelfHeight, DIAM, Color.black)));

        // Draw the first frame
        drawFrame(gameElements);
        int count = 0;

        this.run = true;
        // run until the target is gone (ie, off the right end)
        while ((ball.getX() != LAUNCHER_X || target.getX() < RIGHT_END) && run) {

            // if the ball is over the right end, make a new one.
            if (ball.getX() >= RIGHT_END) {
                this.ball = new Ball(LAUNCHER_X, LAUNCHER_HEIGHT);
                gameElements.remove(this.ball);
                gameElements.add(this.ball);
                count++;
            }

            // move the ball, if it isn't on the launcher
            if (ball.getX() > LAUNCHER_X) {
                this.ball.step();
            }

            // move target if it isn't on the shelf
            if (target.getX() != SHELF_X) {
                target.step();
            }

            // if ball is hitting the target ball on the shelf, then make it start moving
            // too
            double dist = Math.hypot(target.getX() - this.ball.getX(), target.getHeight() - this.ball.getHeight());
            if (target.getX() == SHELF_X && dist <= DIAM) {
                target.setXSpeed(2);
                target.step();
            }

            // redraw the game and pause
            drawFrame(gameElements);

            UI.sleep(40); // pause of 40 milliseconds

        }
        UI.setFontSize(40);
        UI.drawString(count + " tries", 200, 200);
    }

    /**
     * Launch the current ball, if it is still in the catapult,
     * Speed is based on the position of the mouse relative to the ground.
     */
    public void launch(String action, double x, double y) {
        if (this.ball == null) {
            UI.printMessage("Press Core/Completion button first to create a ball");
            return; // the ball hasn't been constructed yet.
        }
        if (this.ball.getX() == LAUNCHER_X && this.ball.getHeight() == LAUNCHER_HEIGHT) {
            double speedX = (x - LAUNCHER_X) / 15;
            double speedUp = (GROUND - LAUNCHER_HEIGHT - y) / 15;
            double speed = Math.hypot(speedUp, speedX);
            // scale down if over the maximum allowed speed
            if (speed > MAX_SPEED) {
                speedUp = speedUp * MAX_SPEED / speed;
                speedX = speedX * MAX_SPEED / speed;
            }
            this.ball.setXSpeed(speedX);
            this.ball.setYSpeed(speedUp);
            this.ball.step();
            this.indicator.hide();
        }
    }

    public void runGameChallengeCompletion() {
        this.run = false;
        ArrayList<GameObject> targets = new ArrayList<>();

        // Show the trajectory indicator
        indicator.show();

        /**
         * Initialises the world.
         * 
         * The "world" is an inheritant of Drawable that is responsible
         * for drawing the walls and the background colour of the map.
         * 
         * It is added first so that every other object draws on top of the background.
         * 
         * An improvement would be to assign priorities to each Drawable object, fixing
         * the draw order
         * and not relying on array order integrity.
         */
        targets.add(new World());
        targets.add(indicator);
        Random nonSeedRandom = new Random();
        Random random = (seed != -1 ? new Random(seed) : new Random());

        /**
         * Initialises the enemies.
         * There will be a random number of enemies, between 5 and 10.
         * Enemies will spawn at a random position somewhere between (x=300, y=300) and
         * (x=350, y=450).
         * Enemies have a 50% chance of simply being a ball, or being an Angry Birds
         * pig.
         * All enemies spawn on a shelf.
         */
        for (int i = 0; i < Math.max(5, Math.floor(random.nextDouble() * 10)); i++) {
            double x = Math.min(300, 50 + random.nextDouble() * 300);
            double y = Math.min(300, 50 + random.nextDouble() * 400);
            if (nonSeedRandom.nextBoolean()) {
                targets.add(new AngryBirdBall(x + BallGame.DIAM / 2, y));
            } else {
                targets.add(new EnemyBall(x + BallGame.DIAM / 2, y));
            }
            targets.add(new Shelf(x, y, DIAM, Color.black));
        }

        /**
         * Initialises the cubes.
         * 
         * Cubes are platform obstacles that are not inheritants of EnemyTarget (i.e.
         * the player does not need
         * to move them off the screen to win), but are collidable physics objects and
         * can be used
         * to push the player and the targets.
         * 
         * Targets have the same spawn range as enemies, and also spawn with a shelf.
         */
        for (int i = 0; i < Math.max(3, Math.floor(random.nextDouble() * 5)); i++) {
            double x = Math.min(300, 50 + random.nextDouble() * 300);
            double y = Math.min(300, 50 + random.nextDouble() * 400);
            targets.add(new Box(x, y, DIAM, DIAM,
                    new Color(nonSeedRandom.nextFloat(), nonSeedRandom.nextFloat(), nonSeedRandom.nextFloat())));
            targets.add(new Shelf(x, y, DIAM, Color.black));
        }

        // Start the game simulation.
        runGameWithTargets(targets);
    }

    /**
     * Run the game with a variable number of game objects in the game.
     * Game objects could be targets (inheritants of EnemyTarget), in which case
     * they will be tracked
     * They could inherit Drawable, and will be drawn to the screen
     * They could inerhit Collidable, in which case the engine will try to calculate
     * if they're colliding and respond appropriately
     * 
     * For example, the world, player launcher, and shelf are all Drawable-only,
     * because
     * we don't need to calculate collisions for them
     * 
     * While players, enemies, and boxes are Collidable, because we need to
     * calculate collisions and simulate a physics response.
     */
    public void runGameWithTargets(ArrayList<GameObject> elements) {
        UI.printMessage("Click the mouse to launch Red.");

        // Initialise the ball, launcher, and the game UI
        this.ball = new PlayerBall(LAUNCHER_X, LAUNCHER_HEIGHT);
        elements.add(new PlayerLauncher());
        elements.add(this.ball);
        GameUI gameUi = new GameUI(MAX_TIME_MSEC);
        elements.add(gameUi);

        // Draw the first frame of the game
        drawFrame(elements);

        int count = 0;
        this.run = true;

        // Frame calculation loop
        // Run while the game hasn't ended
        while (run) {
            // Reset ball if it leaves the bounds
            if (!ball.isInBounds() || gameUi.getTime() > MAX_TIME_MSEC) {
                this.ball.setXSpeed(0);
                this.ball.setYSpeed(0);
                this.ball.setPosition(LAUNCHER_X, LAUNCHER_HEIGHT);

                // Increment the 'tries' counter
                gameUi.incrementTries();
                count++;

                // Show the trajectory indicator
                indicator.show();

                // Reset timer
                gameUi.reset();
            }

            // Increment timer if the ball is moving
            if (ball.isMoving()) {
                gameUi.increment(MSEC_PER_FRAME);
            }

            // Avoid calculating collision if this object has already collided with another
            // one in this frame
            ArrayList<Collidable> collidedObjects = new ArrayList<>();
            // Run simulation for objects that follow the simulation
            for (GameObject gameObject : elements) {
                if (gameObject instanceof PhysicsObject) {
                    // Run physics simulation code
                    PhysicsObject physicsObject = (PhysicsObject) gameObject;

                    /**
                     * Handle collisions.
                     */
                    if (gameObject instanceof Collidable && !collidedObjects.contains((Collidable) gameObject)) {
                        Collidable collidableObject = (Collidable) gameObject;

                        // Iterate over every other collidable object and check if the current one is
                        // colliding.
                        for (Collidable collidedObject : ((Iterable<Collidable>) elements.stream()
                                .filter(e -> e instanceof Collidable // An object has to inherit Collidable to be able
                                                                     // to collide with something.
                                        && e != gameObject // An object can't collide with itself.
                                        && !collidedObjects.contains((Collidable) e) // Don't calculate collisions for
                                                                                     // objects that have already
                                                                                     // collided in this frame.
                                        && checkCollision(collidableObject, ((Collidable) e))) // Is this object
                                                                                               // actually colliding
                                                                                               // with something?
                                .map(e -> ((Collidable) e))::iterator)) {

                            // Calculate the collision angle, and apply
                            // appropriate forces
                            collidedObjects.add(collidedObject);
                            double xDiff = Math.abs(collidableObject.getX() - collidedObject.getX());
                            double yDiff = Math.abs(collidableObject.getHeight() - collidedObject.getHeight());
                            collidableObject.setXSpeed(collidedObject.getXSpeed() * (xDiff / 8));
                            collidableObject.setYSpeed(collidedObject.getYSpeed() * (yDiff / 8));
                        }
                    }

                    // Re-calculate movement for physics objects
                    if (physicsObject.isMoving())
                        physicsObject.step();
                }
            }
            // Redraw the game.
            drawFrame(elements);

            // Check if the game has ended.
            run = ball.getX() != LAUNCHER_X
                    || elements.stream().filter(e -> e instanceof EnemyTarget && ((EnemyTarget) e).isInBounds())
                            .count() > 0;

            // Wait for the next frame.
            UI.sleep(MSEC_PER_FRAME);
        }

        // Player won.
        UI.printMessage("Victory!");
        UI.setFontSize(40);
        UI.drawString(count + " tries", 200, 200);
    }

    /**
     * Compares distance between two collidable objects to see if they are currently
     * colliding.
     */
    public boolean checkCollision(Collidable first, Collidable second) {
        if (first == second)
            return false;
        double dist = Math.hypot(first.getX() - second.getX(), first.getHeight() - second.getHeight());
        return dist <= DIAM;
    }

    /**
     * Draws a single game frame, using a provided list of elements.
     * This method will only draw game objects that inherit Drawable.
     * This method will clear the screen before drawing the frame.
     */
    public void drawFrame(ArrayList<GameObject> objects) {
        UI.clearGraphics();

        UI.setColor(Color.black);
        UI.setLineWidth(2);

        // Draw all drawable game objects
        for (GameObject gameObject : objects) {
            if (gameObject instanceof Drawable) {
                ((Drawable) gameObject).draw();
            }
        }
    }

    // Main
    /** Create a new BallGame object and setup the interface */
    public static void main(String[] arguments) {
        BallGame bg = new BallGame();
        bg.setupGUI();
    }

}
