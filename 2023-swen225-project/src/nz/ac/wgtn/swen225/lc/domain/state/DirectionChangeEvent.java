package nz.ac.wgtn.swen225.lc.domain.state;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

import java.beans.PropertyChangeEvent;

/**
 * Represents an event containing a change in player position and an associated change in their direction.
 * This event is used to pass data from the controller to the renderer containing the change of direction.
 *
 * @author rakenajack
 */
public class DirectionChangeEvent extends PropertyChangeEvent {
    private Direction direction;

    /**
     * The new direction of the player.
     */
    public Direction direction() { return this.direction; }

    public DirectionChangeEvent(GameModel source, Position oldPosition, Position newPosition, Direction direction) {
        super(source, "position", oldPosition, newPosition);
        this.direction = direction;
    }
}
