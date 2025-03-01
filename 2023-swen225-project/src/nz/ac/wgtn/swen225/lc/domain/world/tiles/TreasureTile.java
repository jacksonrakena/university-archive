package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

/**
 * Represents a treasure, collectable by the player.
 *
 * @author rakenajack
 */
public class TreasureTile extends MovableTile {
    @Override
    public void onMoveInto(Position self, Controller controller) {
        controller.model().decrementRemainingTreasures();
        controller.model().write(self, new FreeTile());
    }

    @Override
    public Tile clone() {
        return new TreasureTile();
    }
}
