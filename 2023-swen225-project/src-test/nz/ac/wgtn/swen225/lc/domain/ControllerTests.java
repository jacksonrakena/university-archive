package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

public class ControllerTests {
    private GameModel model;
    private Controller controller;
    @BeforeEach
    public void setup() {
        model = GameModel.newGame();
        controller = new Controller(model);
    }

    @TestFactory
    public Collection<DynamicTest> testInvalidMoveIntoWall() {
        var directions = Arrays.stream(Direction.values()).toList();
        var playerPosition = new Position(3, 3);

        return directions.stream().map(d -> DynamicTest.dynamicTest(d.toString(), () -> {
            this.model.position(playerPosition, Direction.UP);
            this.model.write(playerPosition.add(d.getTranslation()), new WallTile());
            this.controller.tryMoveInDirection(d);
            Assertions.assertEquals(playerPosition, this.model.position());
        })).toList();
    }

    @TestFactory
    public Collection<DynamicTest> testValidMoveIntoFreeTile() {
        var directions = Arrays.stream(Direction.values()).toList();
        var playerPosition = new Position(3, 3);

        return directions.stream().map(d -> DynamicTest.dynamicTest(d.toString(), () -> {
            this.model.position(playerPosition, Direction.UP);
            this.model.write(playerPosition.add(d.getTranslation()), new FreeTile());
            this.controller.tryMoveInDirection(d);
            Assertions.assertEquals(playerPosition.add(d.getTranslation()), this.model.position());
        })).toList();
    }

    @Test
    public void testAgentLoop() throws InterruptedException {
        var c = new Controller(GameModel.newGame());
        Thread.sleep(2000);

    }
}
