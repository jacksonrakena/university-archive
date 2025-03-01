package nz.ac.wgtn.swen225.lc.app.input;

import nz.ac.wgtn.swen225.lc.app.FileSelector;
import nz.ac.wgtn.swen225.lc.app.LarryCroftsAdventures;
import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
import nz.ac.wgtn.swen225.lc.recorder.Replay;
import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;


/**
 * Class that listens for inputs from the keyboard
 *
 * @author renesharma - 300610529
 */
public class GameKeyListener implements KeyListener {

    private final GameModel model;
    private static MainWindow mainWindow;
    private final Controller gameController;
    private final GameTimer clock;
    public static Level[] levels;
    private final HashSet<Integer> sync = new HashSet<>();
    private int lastInputTick = 0;
    private boolean paused = false;
    private int key;

    /**
     * Initialise a new GameKeyListener
     *
     * @param model the state of the game
     * @param timer the timer
     * @param gameController the controller in which the inputs are processed
     * @param persistenceManager how levels are saved
     */
    public GameKeyListener(GameModel model, GameTimer timer, Controller gameController, PersistenceManager persistenceManager) {
        this.model = model;
        this.clock = timer;
        mainWindow = new MainWindow(model);
        this.gameController = gameController;
        mainWindow.addKeyListener(this);
        levels = persistenceManager.loadLevels();
        model.addPropertyChangeListener(evt -> {
            if ("levelNumber".equals(evt.getPropertyName())) {
                int newValue = (int) evt.getNewValue();
                if (newValue == -1) {
                    mainWindow.setPaused(true);
                    mainWindow.getApplicationWindow().getTimer().pause(true);
                    mainWindow.setWinView();
                }
            }
        });
    }

    public void loadReplay(Replay replay) {
        mainWindow.getApplicationWindow().setReplay(replay);
    }

    /**
     * Return the main window
     *
     * @return the mainWindow
     */
    public static MainWindow getMainWindow() {
        return mainWindow;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * gets the key and saves it to a field, then processes the input
     *
     * @param e the event to be processed
     */
    @Override
    public synchronized void keyPressed(KeyEvent e) {
        key = e.getKeyCode();
        onKeyPress(1000, false);
    }

    /**
     * remove the key from the set of keys currently pressed
     *
     * @param e the event to be processed
     */
    @Override
    public synchronized void keyReleased(KeyEvent e) {
        sync.remove(e.getKeyCode());
        key = KeyEvent.CHAR_UNDEFINED;
    }

    /**
     * On key press, will input the key differently depending on whether it's held down
     * or being spam pressed
     *
     * @param delay the minimum delay between each input
     * @param auto dependant if the button is being held down or not
     */

    public void onKeyPress(int delay, boolean auto) {

        if (clock.getGameTick() - lastInputTick < delay && auto)
            return;

        lastInputTick = clock.getGameTick();

        switch (key) {
            case KeyEvent.VK_DOWN -> {
                if (paused)
                    return;
                gameController.tryMoveInDirection(Direction.DOWN);
            }
            case KeyEvent.VK_UP -> {
                if (paused)
                    return;
                gameController.tryMoveInDirection(Direction.UP);
            }
            case KeyEvent.VK_LEFT -> {
                if (paused)
                    return;
                gameController.tryMoveInDirection(Direction.LEFT);
            }
            case KeyEvent.VK_RIGHT -> {
                if (paused)
                    return;
                gameController.tryMoveInDirection(Direction.RIGHT);
            }
            case KeyEvent.VK_CONTROL -> sync.add(key);
            case KeyEvent.VK_X,
                    KeyEvent.VK_S,
                    KeyEvent.VK_R,
                    KeyEvent.VK_1,
                    KeyEvent.VK_2 -> {
                sync.add(key);
                attemptCommand();
            }
            case KeyEvent.VK_ESCAPE -> pauseGame(false);

            case KeyEvent.VK_SPACE -> pauseGame(true);
        }
    }

    /**
     * Pauses entire game, and will pop up the pauseView
     *
     * @param paused if game is paused
     */
    private void pauseGame(boolean paused) {
        this.paused = paused;
        mainWindow.getApplicationWindow().getTimer().pause(paused);
        mainWindow.setPaused(paused);
    }

    /**
     * checks if the command is valid, and then preforms the command
     */
    private void attemptCommand() {
        int command = 0;

        for (Integer key : sync) {
            command += key;
        }

        switch (command) {
            case 99 -> { //CTRL R
                sync.clear();
                FileSelector fileSelector = new FileSelector(mainWindow, false);
                int returnValue = fileSelector.getFileChooser().showOpenDialog(null);

                try {
                    switch (returnValue) {
                        case JFileChooser.APPROVE_OPTION -> {
                            pauseGame(false);
                            File selectedFile = fileSelector.getFileChooser().getSelectedFile();
                            GameModel newGame = new PersistenceManager().loadGameModel(selectedFile);
                            new LarryCroftsAdventures(newGame);
                        }
                        case JFileChooser.CANCEL_OPTION -> {
                            System.out.println("No file selected");
                            pauseGame(false);
                        }

                        case JFileChooser.ERROR_OPTION -> {
                            System.out.println("Error selecting file");
                            pauseGame(false);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            case 100 -> { //CTRL S
                sync.clear();
                File save = new File(System.getProperty("user.dir") + "/exit.json");
                new PersistenceManager().save(gameController.model(), save);
                System.exit(0);
            }
            case 105 -> { //CTRL X
                File save = new File(System.getProperty("user.dir") + "/exit.json");
                new PersistenceManager().save(GameModel.newGameOnLevel(levels[model.levelNumber() - 1]), save);
                System.exit(0);
            }
            case 66 -> {
                sync.clear();
                new LarryCroftsAdventures(GameModel.newGameOnLevel(levels[0]));
            }
            case 67 -> {
                sync.clear();
                new LarryCroftsAdventures(GameModel.newGameOnLevel(levels[1]));
            }
        }
    }
}
