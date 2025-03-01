package nz.ac.wgtn.swen225.lc.domain.world.abs.agents;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;

/**
 * Represents a generic enemy agent on the board.
 */
public abstract class Agent {
    /**
     * The position of the agent.
     */
    public Position position;

    /**
     * The current direction of the agent.
     */
    public Direction direction;

    public Agent(Position position) {
        this.position = position;
        this.direction = Direction.UP;
    }

    /**
     * Ran on every agent turn, usually every second.
     */
    public abstract void onTurn(GameModel model);
}
