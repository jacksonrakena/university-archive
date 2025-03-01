package nz.ac.wgtn.swen225.lc.domain.state;

import com.google.common.collect.ImmutableSet;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents the current state of the game.
 * This class also possesses the ability to update listeners when it changes
 * (through PropertyChangeListener)
 *
 * @author rakenajack
 */
public class GameModel {

    /**
     * The ID of the level currently being played.
     */
    public String levelId() { return this.levelId; }
    private String levelId;
    private Tile[][] tiles = new Tile[100][100];
    private int levelNumber = 0;

    /**
     * The number of the level currently being played. This is used to advance the level.
     */
    public int levelNumber() { return this.levelNumber; }

    private String helpMessage = "";

    /**
     * Sets the help message shown when the player walks over an info tile.
     */
    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
        this.pcs.firePropertyChange("helpMessage", null, this.helpMessage);
    }
    public String helpMessage() { return this.helpMessage; }

    /**
     * Sets the current level number, and updates listeners.
     */
    public void levelNumber(int newLevelNumber) {
        var old = this.levelNumber;
        this.levelNumber = newLevelNumber;
        this.pcs.firePropertyChange("levelNumber", old, this.levelNumber);
    }

    private List<Agent> agents = new ArrayList<>();

    /**
     * Returns a list of all agents and their current state.
     */
    public List<Agent> agents() { return this.agents; }

    /**
     * Sets the list of agents.
     */
    public void setAgents(List<Agent> agents) {
        this.agents = agents;
    }

    /**
     * Notifies all listeners that agent state has changed.
     */
    public void notifyAgentUpdate() {
        this.pcs.firePropertyChange("agents", null, this.agents);
    }

    /**
     * Returns the 2D array representing all tiles in the game.
     * This does not take into account the viewable area.
     */
    public Tile[][] tiles() { return this.tiles; }
    private Set<Color> keys;

    /**
     * Returns a set of all the keys the player holds in their inventory.
     * A player cannot carry more than 1 key of a given colour, so this is implemented
     * as a Set.
     */
    public ImmutableSet<Color> keys() { return ImmutableSet.copyOf(this.keys); }

    private int remainingTreasures;

    /**
     * Returns the number of treasures that the player has yet to collect to complete the level.
     */
    public int remainingTreasures() { return this.remainingTreasures; }

    private Position position;

    /**
     * Returns the current position vector of the player, relative to the level as specified in <code>tiles</code>.
     */
    public Position position() { return this.position; }

    /**
     * Sets the player position.
     * @param pos The position to set the player to.
     * @param dir The direction of translation.
     */
    public void position(Position pos, Direction dir) {
        Position old = this.position;
        this.position = pos;
        this.pcs.firePropertyChange(new DirectionChangeEvent(this, old, pos, dir));
    }

    private GameModel(String levelId,
                      Tile[][] tiles,
                      int remainingTreasures,
                      Position playerPosition,
                      Set<Color> keysInInventory,
                      List<Agent> agents) {
        this.levelId = levelId;
        this.tiles = tiles;
        this.remainingTreasures = remainingTreasures;
        this.position = playerPosition;
        this.keys = keysInInventory;
    }

    /**
     * Creates a new GameModel, loading the first level.
     */
    public static GameModel newGame() {
        return GameModel.newGameOnLevel(new PersistenceManager().loadLevels()[0]);
    }

    /**
     * Creates a new <code>GameModel</code> using information from a provided level.
     * @param levelInfo The level to load into the newly-created GameModel.
     */
    public static GameModel newGameOnLevel(Level levelInfo) {
        var t = levelInfo.generateGameTiles();
        var l = new GameModel(levelInfo.mapId(),
                t,
                (int) Arrays.stream(t).flatMap(Arrays::stream).filter(d -> d instanceof TreasureTile).count(),
                levelInfo.playerSpawnPosition(), new HashSet<>(),
                levelInfo.agents());
        l.insertAgents();
        return l;
    }

    /**
     * Loads level information into this model, overwriting
     * data, copying tiles, and clearing the game state.
     * @param levelInfo The information of the new level to load.
     */
    public void loadLevel(Level levelInfo) {
        this.levelId = levelInfo.mapId();
        Position oldPos = this.position;
        this.position = levelInfo.playerSpawnPosition();
        Tile[][] oldTile = this.tiles;
        this.tiles = levelInfo.generateGameTiles();
        this.agents = new ArrayList<>();
        var oldrt = this.remainingTreasures;

        this.remainingTreasures = (int) Arrays.stream(this.tiles).flatMap(Arrays::stream).filter(d -> d instanceof TreasureTile).count();
        this.pcs.firePropertyChange("remainingTreasures", oldrt, remainingTreasures);
        this.resetKeys();
        insertAgents();

        this.pcs.firePropertyChange(new DirectionChangeEvent(this, oldPos, levelInfo.playerSpawnPosition(), Direction.UP));
        pcs.firePropertyChange("tiles", oldTile, this.tiles);
    }

    /**
     * Inserts up to 5 agents at random free positions in the level.
     */
    public void insertAgents() {
        if (this.levelNumber != 1) return;
        final int AGENT_N_MAX = 5;
        var currentAgents = 0;
        for (int y = 0; y < this.tiles.length; y++) {
            for (int x = 0; x < this.tiles[y].length; x++) {
                if (Math.random() < 0.05 && read(new Position(x,y)) instanceof FreeTile && currentAgents < AGENT_N_MAX) {
                    currentAgents++;
                    this.agents.add(new PersistenceManager().getSimpleEnemy(new Position(x,y)));
                }
            }
        }
        System.out.println("CONTROLLER: Inserted " + currentAgents + " enemies into level " + this.levelId);
    }

    /**
     * Creates an in-progress <code>GameModel</code> from saved game information.
     * @param mapId The ID of the map that is being played.
     * @param tiles The current state of the board.
     * @param remainingTreasures The treasures left to collect.
     * @param playerPosition The position of the player when the game was saved.
     * @param keysInInventory The keys in the player's inventory.
     * @return A <code>GameModel</code> that represents the game as it was when it was saved.
     */
    public static GameModel fromSave(
            String mapId,
            Tile[][] tiles,
            int remainingTreasures,
            Position playerPosition,
            Set<Color> keysInInventory,
            List<Agent> agents
    ) {
        return new GameModel(mapId, tiles, remainingTreasures, playerPosition, keysInInventory, agents);
    }


    /**
     * Lowers remaining treasures by one.
     * @throws IllegalArgumentException Thrown when remaining treasures is equal to zero.
     */
    public void decrementRemainingTreasures() {
        if (this.remainingTreasures == 0) {
            throw new IllegalArgumentException("Cannot decrement remainingTreasures below 0.");
        }

        int oldRemainingTreasures = this.remainingTreasures;
        this.remainingTreasures--;
        pcs.firePropertyChange("remainingTreasures", oldRemainingTreasures, remainingTreasures);
        assert this.remainingTreasures == (oldRemainingTreasures-1);
    }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener instance to listen to model updates.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Determines whether the player currently holds a key of a given colour.
     */
    public boolean hasKey(Color colour) { return keys.contains(colour); }

    /**
     * Stores a given key of a given colour.
     * @throws IllegalArgumentException Thrown if the player already possesses a key of this colour.
     */
    public void storeKey(Color colour) {
        if (keys.contains(colour)) throw new IllegalArgumentException("Cannot store the same colour twice.");
        Set<Color> oldKeys = new HashSet<>(keys);
        keys.add(colour);
        pcs.firePropertyChange("keys", oldKeys, keys);
    }

    /**
     * Updates the list of keys.
     */
    public void setKeys(Set<Color> keys) {
        this.keys = keys;
    }

    /**
     * Drops all keys the player currently holds.
     */
    public void resetKeys() {
        Set<Color> oldKeys = new HashSet<>(keys);
        keys.clear();
        pcs.firePropertyChange("keys", oldKeys, keys);
    }

    /**
     * Reads a position from the board and returns the Tile instance at that position.
     * @return Returns null if the tile is outside the bounds of the map.
     */
    public Tile read(Position position) {
        if (!inBounds(position)) return null;
        return this.tiles[position.y()][position.x()];
    }

    private boolean inBounds(Position position) {
        return position.x() >= 0 &&
                position.y() >= 0 &&
                position.x() < this.tiles.length &&
                position.y() < this.tiles[position.x()].length;
    }

    /**
     * Writes a given Tile instance to the board at the given position,
     * and signals to listeners that the tiles have changed.
     */
    public void write(Position position, Tile replacement) {
        if (!inBounds(position)) return;
        // Install new tile
        Tile old = read(position);
        this.tiles[position.y()][position.x()] = replacement;

        // Alert listeners
        pcs.firePropertyChange(new TileChangeEvent(this, tiles, position, old, this.tiles[position.x()][position().y()]));
    }
}
