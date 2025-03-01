package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

/**
 * An empty tile.
 *
 * @author rakenajack
 */
public class FreeTile extends MovableTile {

    @Override
    public Tile clone() {
        return new FreeTile();
    }
}
