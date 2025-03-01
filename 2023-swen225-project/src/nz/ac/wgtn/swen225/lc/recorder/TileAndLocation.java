package nz.ac.wgtn.swen225.lc.recorder;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

public record TileAndLocation(Position position, Tile tile) {
	
	@Override
	public String toString() {
		return this.position.toString() + " " + this.tile.toString();
	}
}
