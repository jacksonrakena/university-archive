package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.recorder.Replay;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Class that does Persistence related methods n things
 * @author Tyff Habwe - 300604949
 */
public class PersistenceManager {
    private final static File level1 = new File(
            "res/persistency/levels/level1.json"
    );
    private final static File level2 = new File(
            "res/persistency/levels/level2.json"
    );

    /**
     * Constructor to make a PersistenceManager no arguments
     */
    public PersistenceManager() {
    }

    /**
     * Goes through the levels folder and loads all levels saved as JSON into Level object
     *
     * @return Array of Level Objects
     */
    public Level[] loadLevels() {
        Level[] levels = new Level[2];
        try {
            levels[0] = loadLevel(level1);
            levels[1] = loadLevel(level2);
        } catch (IOException e) {
            System.out.println("SOMETHING WENT WRONG LOADING LEVELS");
        }
        return levels;
    }

    /**
     * Load GameModel Object from JSON file
     *
     * @param file file location to load from
     * @return GameModel generated
     * @throws IOException if filePath is incorrect
     */
    public GameModel loadGameModel(File file) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameModel.class, new GameModelDeserializer())
                .setPrettyPrinting()
                .create();

        String jsonContent = Files.readString(file.toPath(), Charset.defaultCharset());
        return gson.fromJson(jsonContent, GameModel.class);
    }

    /**
     * Save a GameModel Object to a specific File location
     *
     * @param model the GameModel to be saved
     * @param file  the file Location to save
     */
    public void save(GameModel model, File file) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameModel.class, new GameModelSerializer())
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(model, writer);
        } catch (IOException e) {
            System.out.println("ERROR WHEN WRITING");
        }
    }

    /**
     * Load a Level Object from a file location
     *
     * @param file file location to load from
     * @return Level Object gotten from the file location
     * @throws IOException if file location is bad
     */
    public Level loadLevel(File file) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Level.class, new LevelDeserializer())
                .setPrettyPrinting()
                .create();
        String jsonContent = Files.readString(file.toPath(), Charset.defaultCharset());
        return gson.fromJson(jsonContent, Level.class);
    }

    /**
     * Save a Level Object to a JSON file
     *
     * @param level the Level Object to save
     * @param file  the file and location to save the JSON
     */
    public void save(Level level, File file) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Level.class, new LevelSerializer())
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(level, writer);
        } catch (IOException e) {
            System.out.println("ERROR WHEN WRITING");
        }
    }

    /**
     * Load a Replay Object from a JSON file
     *
     * @param file the file to take a replay from
     * @return the Replay Object generated
     * @throws IOException if the file location was bad
     */
    public Replay loadReplay(File file) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Replay.class, new ReplayDeserializer())
                .setPrettyPrinting()
                .create();
        String jsonContent = Files.readString(file.toPath(), Charset.defaultCharset());
        return gson.fromJson(jsonContent, Replay.class);
    }

    /**
     * Save a Replay Object to JSON
     *
     * @param replay the Replay Object to be saved
     * @param file   the file to save the Replay Object to
     */
    public void save(Replay replay, File file) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Replay.class, new ReplaySerializer())
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(replay, writer);
        } catch (IOException e) {
            System.out.println("ERROR WHEN WRITING");
        }
    }

    /**
     * Gets a simple enemy class
     * @param p The position for the SimpleEnemy to spawn at
     * @return Agent that is a SimpleEnemy
     */
    public Agent getSimpleEnemy(Position p) {
        try {
            File jar = new File("res/persistency/levels/level2.jar");

            URLClassLoader child =
                    new URLClassLoader(
                            new URL[]{jar.toURI().toURL()},
                            PersistenceManager.class.getClassLoader());

            Class<?> clazz =
                    Class.forName(
                            "SimpleEnemy",
                            true,
                            child);

            Constructor<?> ctor = clazz.getConstructor(Position.class);

            return (Agent) ctor.newInstance(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the URL of the badactor image in the level2.jar
     * @return URL of the PNG for the badactor sprite sheet
     * @throws MalformedURLException
     */
    public URL getBadActorImage() throws MalformedURLException {
        File jarFile = new File("res/persistency/levels/level2.jar");
        URLClassLoader child =
                new URLClassLoader(
                        new URL[]{jarFile.toURI().toURL()},
                        PersistenceManager.class.getClassLoader());

        return child.getResource("BadActorSpriteSheet.png");
    }
}