package nz.ac.wgtn.swen225.lc.domain.world.level;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a loadable level, which includes a map of tiles, a player spawn position, and spawn positions for agents.
 * @param mapId The ID of this map.
 * @param originalTiles The board of tiles of this map.
 * @param playerSpawnPosition The position where the player spawns.
 * @param agents A list of all agents to be loaded into the level.
 *
 *
 * @author rakenajack
 */
public record Level(String mapId, Tile[][] originalTiles, Position playerSpawnPosition, List<Agent> agents) {
    public Tile[][] generateGameTiles() {
        Tile[][] gameTiles = new Tile[originalTiles.length][originalTiles.length];
        for (int x = 0; x < gameTiles.length; x++) {
            for (int y = 0; y < gameTiles[x].length; y++) {
                gameTiles[x][y] = originalTiles[x][y].clone();
            }
        }
        return gameTiles;
    }
}
