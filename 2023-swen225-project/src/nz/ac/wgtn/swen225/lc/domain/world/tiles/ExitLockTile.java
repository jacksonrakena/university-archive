package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

/**
 * Represents a locked door, usually placed before an exit. The player can move into this tile
 * if they have collected all treasures in the level.
 * @author rakenajack
 */
public class ExitLockTile extends MovableTile {
    @Override
    public boolean canMoveInto(Controller controller) {
        return controller.model().remainingTreasures() == 0;
    }

    @Override
    public Tile clone() {
        return new ExitLockTile();
    }

    @Override
    public void onMoveInto(Position self, Controller controller) {
        controller.model().write(self, new FreeTile());
    }
}
