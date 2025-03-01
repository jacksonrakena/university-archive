package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;

/**
 * Represents an exit. When the player touches this tile,
 * they will be warped to the next level, or the 'You Win!' screen.
 * @author rakenajack
 */
public class ExitTile extends MovableTile {
    @Override
    public void onMoveInto(Position self, Controller controller) {
        var levels = new PersistenceManager().loadLevels();
        if (levels.length-1 == controller.model().levelNumber()) {
            controller.model().levelNumber(-1);
        }
        else {
            controller.model().levelNumber(controller.model().levelNumber()+1);
            controller.model().loadLevel(levels[controller.model().levelNumber()]);
        }
    }

    @Override
    public Tile clone() {
        return new ExitTile();
    }
}
