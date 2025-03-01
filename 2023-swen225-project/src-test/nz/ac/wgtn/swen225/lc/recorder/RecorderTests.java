package nz.ac.wgtn.swen225.lc.recorder;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;

import nz.ac.wgtn.swen225.lc.recorder.*;
import nz.ac.wgtn.swen225.lc.app.LarryCroftsAdventures;
import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.app.input.GameKeyListener;
import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.FreeTile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.KeyTile;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManagerTests;
import nz.ac.wgtn.swen225.lc.recorder.PositionAndDirection;

import org.junit.jupiter.api.Test;

public class RecorderTests {

	private GameModel model;
//    private final static File test1json = new File("Replay.json");
    private final static File levelTestFile = new File("res/persistency/levels/leveltest.json");
	Controller gameController;
	PersistenceManager persistenceManager;
	GameKeyListener listener;
	Recorder recorder;
	GameTimer timer;
	ReplayPlayer rp;

    @BeforeEach
    void setUpTest() {
    	this.model = GameModel.newGame();
		this.timer = new GameTimer();
    	this.gameController = new Controller(model);
    	this.persistenceManager = new PersistenceManager();
    	this.listener = new GameKeyListener(model, timer, gameController, persistenceManager);
    	this.recorder = new Recorder(model);
    	this.rp = new ReplayPlayer(model, recorder.getReplay().replay());
    	System.out.println("\n\nStarting New Test \n\n");
    }

	@Test
	void test1() throws InterruptedException {
		model.position(new Position(2,3), Direction.DOWN);
		model.position(new Position(2,4), Direction.UP);
		rp.play();
	}

	@Test
	void test2() throws InterruptedException {
		model.storeKey(Color.BLUE);
		model.storeKey(Color.RED);
		model.storeKey(Color.GREEN);
//		rp.play();
	}

	@Test
	void test3() throws InterruptedException {
		model.decrementRemainingTreasures();
		model.decrementRemainingTreasures();
		rp.play();
	}

	@Test
	void test4() throws InterruptedException {
		model.write(new Position(3,4), new FreeTile().clone());
		model.decrementRemainingTreasures();
		model.decrementRemainingTreasures();
		Thread.sleep(50);
		rp.realTime();
		rp.play();
	}

	@Test
	void test5() throws InterruptedException, IOException {

		doMoves(gameController);
		Thread.sleep(5000);
		recorder.saveReplay();
		Replay newReplay = persistenceManager.loadReplay(new File(recorder.getReplayPath() + "/replay.json"));
//		GameModel newModel = persistenceManager.loadGameModel(level);
		ReplayPlayer rp = new ReplayPlayer(model, newReplay.replay());
		Thread.sleep(100);
		rp.setTime(500);
		rp.realTime();
		rp.play();
		Thread.sleep(5000);
	}

	@Test
	void test6() throws InterruptedException, IOException {

		GameModel newModel = GameModel.newGame();
		ReplayPlayer rp = new ReplayPlayer(newModel, recorder.getReplay().replay());
		persistenceManager.save(recorder.getReplay(), new File("./modelreplay.json"));
		rp.setTime(500);
		rp.realTime();

	}

	@Test
	void test7() throws IOException, InterruptedException{
		Replay replay = RecorderTests.createReplay();
		File replayPath = new File("./modelreplay.json");
		persistenceManager.save(replay, replayPath);
		Replay replayCopy = persistenceManager.loadReplay(replayPath);
		ReplayPlayer rp2 = new ReplayPlayer(model, replayCopy.replay());
		rp2.setTime(500);
		rp2.play();
		Thread.sleep(5000);
	}

	@Test
	void test8() throws InterruptedException {
		doMoves(gameController);
		Thread.sleep(2000);
		recorder.saveReplay();
		Thread.sleep(50);
		assertTrue(ReplayPlayer.findValidReplay(recorder.getReplayPath()));
	}

	@Test
	void test9() throws InterruptedException {
		doMoves(gameController);
		Thread.sleep(2000);
		rp.stepByStep();
		Thread.sleep(1000);
		rp.stepByStep();
		Thread.sleep(1000);
		rp.stepByStep();
		Thread.sleep(1000);
		rp.stepByStep();

	}


	public void doMoves(Controller gameController) throws InterruptedException {

		Thread.sleep(350);
		gameController.tryMoveInDirection(Direction.DOWN);
		gameController.tryMoveInDirection(Direction.RIGHT);
		gameController.tryMoveInDirection(Direction.DOWN);
		Thread.sleep(250);
		gameController.tryMoveInDirection(Direction.RIGHT);
		gameController.tryMoveInDirection(Direction.DOWN);
		gameController.tryMoveInDirection(Direction.RIGHT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.RIGHT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.RIGHT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.RIGHT);
		Thread.sleep(250);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.LEFT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.LEFT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.LEFT);
		gameController.tryMoveInDirection(Direction.UP);
		gameController.tryMoveInDirection(Direction.LEFT);
		Thread.sleep(250);
		gameController.tryMoveInDirection(Direction.DOWN);
		gameController.tryMoveInDirection(Direction.LEFT);
		gameController.tryMoveInDirection(Direction.DOWN);
		gameController.tryMoveInDirection(Direction.LEFT);
		gameController.tryMoveInDirection(Direction.DOWN);
		gameController.tryMoveInDirection(Direction.LEFT);
	}

//	@Test
//	void test2() throws InterruptedException {
//		LarryCroftsAdventures game = new LarryCroftsAdventures() {
//			private Recorder recorder = new Recorder(this.model);
//			private GameModel model;
//			};
//		new Recorder(model, test1json);
//		model.position(new Position(2,3));
//		model.position(new Position(2,4));
//	}

	/**
     * Helper method to create and fill a Replay Object to test with
     *
     * @return specific Replay Object
     */
    public static Replay createReplay() {
        TreeMap<Integer, HashMap<String, Object>> replayData = new TreeMap<>();
        PersistenceManager persistenceManager = new PersistenceManager();

        // Adding keys and values as per the provided data
        replayData.put(0, new HashMap<>());
        replayData.put(31, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(5, 4), Direction.DOWN))));
        replayData.put(32, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(5, 5), Direction.DOWN))));
        replayData.put(41, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 5), Direction.DOWN))));
        replayData.put(60, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 6), Direction.DOWN))));
        replayData.put(85, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 7), Direction.DOWN))));
        replayData.put(87, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 8), Direction.DOWN))));
        replayData.put(90, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 9), Direction.DOWN))));
        replayData.put(93, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(3, 9), Direction.DOWN))));
        replayData.put(108, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 9), Direction.DOWN))));
        replayData.put(117, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(1, 9), Direction.DOWN))));
        replayData.put(124,
                new HashMap<>(
                        Map.of(
                                "keys", List.of(new Color(69, 69, 160)),
                                "position", new Position(1, 10),
                                "tile at: (1, 10)",
                                new TileAndLocation(
                                        new Position(1, 10),
                                        new KeyTile(new Color(69, 69, 160))
                                ),
                                "agents", List.of(persistenceManager.getSimpleEnemy(new Position(2, 3)), persistenceManager.getSimpleEnemy(new Position(3, 2)), persistenceManager.getSimpleEnemy(new Position(3, 2)))
                        )
                )
        );
        replayData.put(141, new HashMap<>(Map.of("position", new Position(2, 10))));
        replayData.put(158, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 9), Direction.DOWN))));
        replayData.put(169, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 8), Direction.DOWN))));
        replayData.put(176, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 7), Direction.DOWN))));
        replayData.put(183, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 6), Direction.DOWN))));
        replayData.put(192, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(2, 5), Direction.DOWN))));
        replayData.put(203, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(3, 5), Direction.DOWN))));
        replayData.put(224, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(3, 4), Direction.DOWN))));
        replayData.put(233, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 4), Direction.DOWN))));
        replayData.put(245,
                new HashMap<>(
                        Map.of(
                                "positionAndDirection", new PositionAndDirection(new Position(4, 3), Direction.DOWN),
                                "tile at: (4, 3)",
                                new TileAndLocation(
                                        new Position(4, 3),
                                        new FreeTile()
                                )
                        )
                )
        );

        replayData.put(256,
                new HashMap<>(
                        Map.of(
                                "remainingTreasures", 1,
                                "tile at: (4, 2)",
                                new TileAndLocation(
                                        new Position(4, 2),
                                        new FreeTile()
                                ),
                                "positionAndDirection", new PositionAndDirection(new Position(4, 2), Direction.DOWN)
                        )
                )
        );
        replayData.put(267, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 1), Direction.DOWN))));
        replayData.put(278, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 2), Direction.DOWN))));
        replayData.put(288, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 3), Direction.DOWN))));
        replayData.put(297, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 4), Direction.DOWN))));
        replayData.put(304, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(5, 4), Direction.DOWN))));
        replayData.put(316, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(6, 4), Direction.DOWN))));
        replayData.put(325, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(6, 3), Direction.DOWN))));
        replayData.put(341, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(7, 3), Direction.DOWN))));
        replayData.put(351, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(7, 2), Direction.DOWN))));
        replayData.put(353, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(7, 3), Direction.DOWN))));
        replayData.put(363, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(7, 2), Direction.DOWN))));
        replayData.put(373, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(7, 1), Direction.DOWN))));
        replayData.put(386, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(8, 1), Direction.DOWN))));
        replayData.put(405, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(9, 1), Direction.DOWN))));

        replayData.put(420,
                new HashMap<>(
                        Map.of(
                                "keys", List.of(new Color(69, 69, 160), new Color(126, 211, 33)),
                                "position", new Position(10, 1),
                                "tile at: (10, 1)",
                                new TileAndLocation(
                                        new Position(10, 1),
                                        new FreeTile()
                                )
                        )
                )
        );
        replayData.put(440, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(9, 1), Direction.DOWN))));
        replayData.put(450, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(8, 1), Direction.DOWN))));
        replayData.put(458, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(8, 2), Direction.DOWN))));

        return new Replay(replayData);
    }
    /**
     * Helper method to create and fill a Replay Object to test with
     *
     * @return specific Replay Object
     */
    public static Replay createSimpleReplay() {
        TreeMap<Integer, HashMap<String, Object>> replayData = new TreeMap<>();

        // Adding keys and values as per the provided data
        replayData.put(0, new HashMap<>());
        replayData.put(31, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(5, 4), Direction.DOWN))));
        replayData.put(117, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(1, 9), Direction.DOWN))));
        replayData.put(124,
                new HashMap<>(
                        Map.of(
                                "keys", List.of(new Color(69, 69, 160)),
                                "position", new Position(1, 10),
                                "tile at: (1, 10)",
                                new TileAndLocation(
                                        new Position(1, 10),
                                        new KeyTile(new Color(69, 69, 160))
                                )
                        )
                )
        );
        replayData.put(141, new HashMap<>(Map.of("position", new Position(2, 10))));
        replayData.put(233, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 4), Direction.DOWN))));
        replayData.put(245,
                new HashMap<>(
                        Map.of(
                                "positionAndDirection", new PositionAndDirection(new Position(4, 3), Direction.DOWN),
                                "tile at: (4, 3)",
                                new TileAndLocation(
                                        new Position(4, 3),
                                        new FreeTile()
                                )
                        )
                )
        );

        replayData.put(256,
                new HashMap<>(
                        Map.of(
                                "remainingTreasures", 1,
                                "tile at: (4, 2)",
                                new TileAndLocation(
                                        new Position(4, 2),
                                        new FreeTile()
                                ),
                                "positionAndDirection", new PositionAndDirection(new Position(4, 2), Direction.DOWN)
                        )
                )
        );
        replayData.put(267, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(4, 1), Direction.DOWN))));
        replayData.put(405, new HashMap<>(Map.of("positionAndDirection", new PositionAndDirection(new Position(9, 1), Direction.DOWN))));

        replayData.put(420,
                new HashMap<>(
                        Map.of(
                                "keys", List.of(new Color(69, 69, 160), new Color(126, 211, 33)),
                                "position", new Position(10, 1),
                                "tile at: (10, 1)",
                                new TileAndLocation(
                                        new Position(10, 1),
                                        new FreeTile()
                                )
                        )
                )
        );

        return new Replay(replayData);
    }
}
