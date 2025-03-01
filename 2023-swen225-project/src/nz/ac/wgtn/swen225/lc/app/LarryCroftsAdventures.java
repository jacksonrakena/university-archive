package nz.ac.wgtn.swen225.lc.app;

import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.app.input.GameKeyListener;
import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
import nz.ac.wgtn.swen225.lc.recorder.Replay;

import java.io.File;
import java.io.IOException;

/**
 * Class to define a new game of Larry Crofts Adventures, each constructor is a different way of loading up the game
 *
 * @author renesharma - 300610529
 */
public class LarryCroftsAdventures {

    private GameModel model;
    private final Controller gameController;
    private final PersistenceManager persistenceManager;
    private final GameKeyListener listener;
    private final GameTimer timer;

    /**
     * initialise a new game of Larry Crofts Adventure with no arguments
     */
    public LarryCroftsAdventures() {
        File toLoad = new File(System.getProperty("user.dir") + "/exit.json");

        this.model = GameModel.newGame();

        if (toLoad.exists() && !toLoad.isDirectory()) {
            try {
                this.model = new PersistenceManager().loadGameModel(toLoad);
                toLoad.delete();
            } catch (IOException e) {
                System.out.println("Failed to load previous save");
                new LarryCroftsAdventures();
            }
        }
        this.timer = new GameTimer();
        this.gameController = new Controller(model);
        this.persistenceManager = new PersistenceManager();
        this.listener = new GameKeyListener(model, timer, gameController, persistenceManager);
        this.timer.addGameKeyListener(listener);
    }

    /**
     * Used to load a new game or save from the saved state of the GameModel
     *
     * @param model current game model which is going to be loaded
     */
    public LarryCroftsAdventures(GameModel model) {
        if (GameKeyListener.getMainWindow() != null) {
            GameKeyListener.getMainWindow().getBoardView().soundControl.stopSong();
            GameKeyListener.getMainWindow().dispose();
        }
        this.timer = new GameTimer();
        this.model = model;
        this.gameController = new Controller(model);
        this.persistenceManager = new PersistenceManager();
        this.listener = new GameKeyListener(model, timer, gameController, persistenceManager);
        this.timer.addGameKeyListener(listener);
    }

    /**
     * Used to load a new game or save from a replay folder
     *
     * @param model current state of the game
     * @param replay the replay to be loaded
     */
    public LarryCroftsAdventures(GameModel model, Replay replay) {
        if (GameKeyListener.getMainWindow() != null) {
            GameKeyListener.getMainWindow().getBoardView().soundControl.stopSong();
            GameKeyListener.getMainWindow().dispose();
        }
        this.timer = new GameTimer();
        this.model = model;
        this.gameController = new Controller(model);
        this.persistenceManager = new PersistenceManager();
        this.listener = new GameKeyListener(model, timer, gameController, persistenceManager);
        this.timer.addGameKeyListener(listener);
        this.listener.loadReplay(replay);
    }
}
