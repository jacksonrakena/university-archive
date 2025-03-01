package nz.ac.wgtn.swen225.lc.recorder;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;

public class DummyEnemy extends Agent {
	
	/**
	 * Spawns in dummy enemy at specified location facing up.
	 * 
	 * @param position - {@link Position}
	 */
	public DummyEnemy(Position position) {
		super(position);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Spawns in dummy enemy at specified location and direction
	 * 
	 * @param position - {@link Position}
	 * @param direction - {@link Direction}
	 */
	public DummyEnemy(Position position, Direction direction) {
		super(position);
		this.direction = direction;
	}
	
	/**
	 * Spawns in dummy enemy at location defined by PositionAndDirection pad.
	 * 
	 * @param pad - {@link PositionAndDirection}
	 */
	public DummyEnemy(PositionAndDirection pad) {
		super(pad.position());
		this.direction = pad.direction();
	}

	@Override
	public void onTurn(GameModel model) {
		//do nothing so dummy enemy has no AI
		return;
	}

}
