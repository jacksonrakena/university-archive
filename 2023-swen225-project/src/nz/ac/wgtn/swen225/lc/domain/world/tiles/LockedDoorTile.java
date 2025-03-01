package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

import java.awt.*;
import java.util.concurrent.locks.Lock;

/**
 * Represents a locked door, which requires a key of a specified colour.
 *
 * @author rakenajack
 */
public class LockedDoorTile extends MovableTile {
    public Color colour;

    /**
     * Returns the colour of the key required to unlock this door.
     */
    public Color getColour() { return this.colour; }

    public LockedDoorTile(Color colour) {
        this.colour = colour;
    }

    @Override
    public void onMoveInto(Position self, Controller controller) {
        controller.model().write(self, new FreeTile());
    }

    @Override
    public boolean canMoveInto(Controller controller) {
        return controller.model().hasKey(this.colour);
    }

    @Override
    public Tile clone() {
        return new LockedDoorTile(colour);
    }
}
