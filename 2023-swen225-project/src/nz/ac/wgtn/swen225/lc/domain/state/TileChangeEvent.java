package nz.ac.wgtn.swen225.lc.domain.state;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

import java.beans.PropertyChangeEvent;

/**
 * An event indicating to listeners that the 2D tiles array has been updated.
 *
 * @author rakenajack
 */
public class TileChangeEvent extends PropertyChangeEvent {
    private final Tile[][] tiles;
    private final Position position;
    private final Tile oldTile;
    private final Tile newTile;

    /**
     * The new tiles array.
     */
    public Tile[][] tiles() { return this.tiles; }

    /**
     * The position on the board that was changed.
     */
    public Position position() { return this.position; }

    /**
     * The old tile.
     */
    public Tile oldTile() { return this.oldTile; }

    /**
     * The new tile.
     */
    public Tile newTile() { return this.newTile; }
    public TileChangeEvent(GameModel source, Tile[][] array, Position changed, Tile oldValue, Tile newValue) {
        super(source, "tiles", oldValue, newValue);
        this.tiles = array;
        this.position = changed;
        this.oldTile = oldValue;
        this.newTile = newValue;
    }
}
