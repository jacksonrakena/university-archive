package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

import java.awt.*;

/**
 * A tile containing a key of a specified colour.
 *
 * @author rakenajack
 */
public class KeyTile extends MovableTile {
    private Color colour;
    public KeyTile(Color colour) {
        this.colour = colour;
    }

    /**
     * Returns the colour of the key within this tile.
     */
    public Color getColour() { return this.colour; }
    @Override
    public void onMoveInto(Position self, Controller controller) {
        controller.model().storeKey(this.getColour());
        controller.model().write(self, new FreeTile());
    }

    @Override
    public Tile clone() {
        return new KeyTile(colour);
    }
}
