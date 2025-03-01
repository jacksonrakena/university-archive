package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.stream.Stream;

public class TileTests {
    @Test
    public void testInfoTile() {
        var tile = new InfoTile("Test");
        Assertions.assertEquals("Test", tile.helpMessage());
    }

    @TestFactory
    public Collection<DynamicTest> testCloneTiles() {
        return Stream.of(
                ExitLockTile.class,
                ExitTile.class,
                FreeTile.class,
                TreasureTile.class,
                WallTile.class
        ).map(clazz -> DynamicTest.dynamicTest(clazz.getSimpleName(), () -> {
            Constructor<?> ctor = clazz.getConstructor();

            var tile = (Tile) ctor.newInstance();
            var clone = tile.clone();
            Assertions.assertNotEquals(clone, tile);
        })).toList();
    }
}
