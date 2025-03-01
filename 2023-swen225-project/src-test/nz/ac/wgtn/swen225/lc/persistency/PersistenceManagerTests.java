package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.JsonObject;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.persistency.exceptions.TileTypeNotFoundException;
import nz.ac.wgtn.swen225.lc.recorder.RecorderTests;
import nz.ac.wgtn.swen225.lc.recorder.Replay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceManagerTests {
    private GameModel model;
    private PersistenceManager persistenceManager;
    private final static File test1json = new File(
            "src-test/nz/ac/wgtn/swen225/lc/persistency/testfiles/test1.json");
    private final static File levelTestFile = new File("" +
            "src-test/nz/ac/wgtn/swen225/lc/persistency/testfiles/level1.json");
    private final static File recorderTestFile = new File("" +
            "src-test/nz/ac/wgtn/swen225/lc/persistency/testfiles/recordertest.json");

    @BeforeEach
    public void setupModel() {
        model = GameModel.newGame();
        persistenceManager = new PersistenceManager();
    }

    @Test
    public void testThatICanSaveABasicGameModel() {
        model.storeKey(Color.BLACK);
        model.storeKey(Color.BLUE);
        model.storeKey(Color.RED);
        model.storeKey(Color.CYAN);

        persistenceManager.save(model, test1json);
    }

    @Test
    public void testThatICanCreateAGameModelFromJSON() {
        GameModel modelResult = null;
        try {
            modelResult = persistenceManager.loadGameModel(test1json);
        } catch (IOException e) {
            System.out.println("INVALID FILENAME OR FILEPATH");
        }
        Position correctPos = new Position(5,5);
        int correctRemainingTreasures = 2;
        String correctLevelID = "level1";

        Set<Color> correctKeys = new HashSet<Color>(
                Arrays.asList(
                        Color.BLUE,
                        Color.BLACK,
                        Color.RED,
                        Color.CYAN
                )
        );

        assert modelResult != null;
        assertEquals(modelResult.position(), correctPos);
        assertEquals(modelResult.remainingTreasures(), correctRemainingTreasures);
        assertEquals(modelResult.levelId(), correctLevelID);
        assertEquals(modelResult.keys(), correctKeys);
    }

    @Test
    public void testThatICanSaveASimpleLevel() {
        Level randomLevel = new Level("testLevel", model.tiles(), new Position(2, 3), List.of());
        persistenceManager.save(randomLevel, levelTestFile);
    }

    @Test
    public void testThatICanLoadASimpleLevel() {
        Level levelResult = null;
        try {
            levelResult = persistenceManager.loadLevel(levelTestFile);
        } catch (IOException e) {
            System.out.println("INVALID FILENAME OR FILEPATH");
        }
        String correctMapID = "testLevel";
        Position correctPosition = new Position(2, 3);
        int tileLength = 12;

        assert levelResult != null;
        assertEquals(levelResult.mapId(), correctMapID);
        assertEquals(levelResult.playerSpawnPosition(), correctPosition);
        assertEquals(levelResult.originalTiles().length, tileLength);
    }

    @Test
    public void testThatICanSaveASimpleRecording() {
        Replay replay = RecorderTests.createReplay();
        persistenceManager.save(replay, recorderTestFile);
    }

    @Test
    public void testThatICanAccessASimpleRecording() {
        Replay replay = null;
        try {
            replay = persistenceManager.loadReplay(recorderTestFile);
        } catch (IOException ignored) {}

        assert replay != null;
    }
    @Test
    public void testThatICanLoadASimpleEnemyFromJar() {
        Agent simpleEnemy = persistenceManager.getSimpleEnemy(new Position(5, 5));
    }
    @Test
    public void testThatICannotDeseriliazeWrongTileType() {
        JsonObject testObject = new JsonObject();
        testObject.addProperty("tileType", "NotARealTile");

        TileTypeNotFoundException thrown = Assertions.assertThrows(TileTypeNotFoundException.class,
                () -> {GameModelDeserializer.deserializeTile(testObject);}
        );
        Assertions.assertEquals("Could not deserialize that Tile Type", thrown.getMessage());
    }
}
