package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;

import javax.swing.*;

/**
 * The Controller class controls game logic, behaviour, and interacting with data (in the model).
 * @author jacksonrakena
 */
public class Controller {
    private GameModel model;
    private javax.swing.Timer timer;

    /**
     * Creates a new Controller instance, responsible for a given GameModel instance.
     */
    public Controller(GameModel model) {
        this.model = model;
        this.timer = new Timer(1000, (t) -> {
            for (var agent : this.model.agents()) {
                agent.onTurn(this.model);
            }
            model.notifyAgentUpdate();
        });
        this.timer.setRepeats(true);
        this.timer.start();
    }

    /**
     * Returns the current operating game model.
     */
    public GameModel model() { return this.model; }


    /**
     * Attempts to move the player in a specified direction.
     * This method is called by the App module's input controller.
     * This method will validate the movement and perform relevant checks and operations.
     */
    public void tryMoveInDirection(Direction direction) {
        Position newPosition = this.model().position().add(direction.getTranslation());
        Tile tile = this.model().read(newPosition);
        if (tile == null) return;

        // Do not move onto a tile if the tile rejects this movement
        if (!tile.canMoveInto(this)) return;

        // Kill the player if they attempt to move into an enemy
        if (this.model.agents().stream().anyMatch(e -> e.position.equals(newPosition))) {
            this.model.loadLevel(new PersistenceManager().loadLevels()[this.model.levelNumber()]);
            return;
        }

        // Set the new position
        this.model().position(newPosition, direction);
        System.out.println("CONTROLLER: Moved to " + newPosition + " on " + tile.getClass().getSimpleName());

        // Perform on-move actions if the tile contains logic for that behaviour.
        if (tile instanceof MovableTile mt) {
            mt.onMoveInto(newPosition, this);
        }
    }
}
