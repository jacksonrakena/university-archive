package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

/**
 * Represents a wall tile, which cannot be traversed into by the player.
 *
 * @author rakenajack
 */
public class WallTile extends Tile {
    @Override
    public Tile clone() {
        return new WallTile();
    }
}
