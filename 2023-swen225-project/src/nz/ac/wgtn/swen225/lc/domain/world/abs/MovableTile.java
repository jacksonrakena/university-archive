package nz.ac.wgtn.swen225.lc.domain.world.abs;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;

/**
 * Represents a tile that has logic applied when the player moves into the tile.
 *
 * @author rakenajack
 */
public abstract class MovableTile extends Tile {
    /**
     * Executed when the player moves into this tile.
     * @param self The position of this tile.
     * @param controller The controller that initiated this action.
     */
    public void onMoveInto(Position self, Controller controller) {}

    @Override
    public boolean canMoveInto(Controller controller) { return true; }
}
