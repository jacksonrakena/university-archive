package nz.ac.wgtn.swen225.lc.domain.world.abs;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;

/**
 * Represents a tile in the world.
 * @author rakenajack
 */
public abstract class Tile implements  Cloneable{
    /**
     * Executed when the player attempts to move into this tile.
     * If this method returns false, the controller will not allow the player
     * to move into this tile.
     * By default, players cannot move into tiles.
     */
    public boolean canMoveInto(Controller controller) { return false; }

    @Override
    public abstract Tile clone();
}

