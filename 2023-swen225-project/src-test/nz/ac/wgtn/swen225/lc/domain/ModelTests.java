package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

public class ModelTests {
    private GameModel model;
    class FakeUpdateListener implements PropertyChangeListener {
        public boolean success;
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            success = true;
        }
    }
    @BeforeEach
    public void setupModel() {
        model = GameModel.newGame();
    }
    @Test
    public void testPropertyChangeEvent() {
        var listener = new FakeUpdateListener();
        model.addPropertyChangeListener(listener);
        model.position(new Position(1,1), Direction.UP);
        Assertions.assertTrue(listener.success);
    }

    @Test
    public void testValidTreasureDecrement() {
        int treasures = model.remainingTreasures();
        model.decrementRemainingTreasures();
        Assertions.assertEquals(treasures-1, model.remainingTreasures());
    }

    @Test
    public void testInvalidTreasureDecrement() {
        int treasures = model.remainingTreasures();
        for (int i = treasures; i > 0; i--) {
            model.decrementRemainingTreasures();
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            model.decrementRemainingTreasures();
        });
    }

    @Test
    public void testResetKeys() {
        model.storeKey(Color.BLUE);
        model.storeKey(Color.BLACK);
        Assertions.assertEquals(2, model.keys().size());
        model.resetKeys();
        Assertions.assertEquals(0, model.keys().size());
    }

    @Test
    public void testKeysStartEmpty() {
        Set<Color> keys = model.keys();
        Assertions.assertEquals(0, keys.size());
    }

    @ParameterizedTest
    @CsvSource({"BLUE", "RED", "ORANGE", "PINK", "MAGENTA", "YELLOW"})
    public void testKeyStore(String input) throws NoSuchFieldException, IllegalAccessException {
        var c = (Color) Color.class.getDeclaredField(input).get(null);
        model.storeKey(c);
        Assertions.assertEquals(1, model.keys().size());
        Assertions.assertTrue(model.hasKey(c));

    }

    @ParameterizedTest
    @CsvSource({"BLUE", "RED", "ORANGE", "PINK", "MAGENTA", "YELLOW"})
    public void testInvalidKeyStoreFails(String input) throws NoSuchFieldException, IllegalAccessException {
        var c = (Color) Color.class.getDeclaredField(input).get(null);
        model.storeKey(c);

        Assertions.assertThrows(IllegalArgumentException.class, () -> model.storeKey(c));
    }

    @Test
    public void testLoadLevel() {
        var gm = GameModel.newGame();
        gm.loadLevel(new PersistenceManager().loadLevels()[gm.levelNumber()+1]);

        gm.insertAgents();
    }
}
