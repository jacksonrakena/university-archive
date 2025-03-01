package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.DirectionChangeEvent;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.state.TileChangeEvent;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.FreeTile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.WallTile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventTests {
    @Test
    public void testDirectionChangeEvent() {
        var dce = new DirectionChangeEvent(GameModel.newGame(), null, new Position(2,2), Direction.UP);
        Assertions.assertEquals(Direction.UP, dce.direction());
    }

    @Test
    public void testTileChangeEvent() {
        Tile[][] t = new Tile[25][];
        Position p = new Position(2,2);
        Tile old = new FreeTile();
        Tile new0 = new WallTile();

        var tce = new TileChangeEvent(GameModel.newGame(), t, p, old, new0);
        Assertions.assertTrue(t == tce.tiles());
        Assertions.assertEquals(p, tce.position());
        Assertions.assertEquals(old, tce.oldTile());
        Assertions.assertEquals(new0, tce.newTile());
    }
}
