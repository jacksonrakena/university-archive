package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;
import nz.ac.wgtn.swen225.lc.persistency.exceptions.TileTypeNotFoundException;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that assists in deserializing a GameModel
 *
 * @author Tyff Habwe - 300604949
 */
public class GameModelDeserializer implements JsonDeserializer<GameModel> {
    /**
     * Deserializes a GameModel from JSON
     *
     * @param jsonElement                a java abstraction of the JSON file
     * @param type                       Object type to change to
     * @param jsonDeserializationContext class to use default deserialization tactic
     * @return GameModel generated
     * @throws JsonParseException if something went wrong
     */
    @Override
    public GameModel deserialize(JsonElement jsonElement,
                                 Type type,
                                 JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String levelId = jsonObject.get("levelId").getAsString();
        int remainingTreasures = jsonObject.get("remainingTreasures").getAsInt();
        Position position = jsonDeserializationContext.deserialize(jsonObject.get("position"), Position.class);

        JsonArray keysAsJson = jsonObject.getAsJsonArray("keys");
        Set<Color> keys = new HashSet<>();

        for (JsonElement e : keysAsJson) {
            keys.add(new Color(e.getAsInt()));
        }

        JsonArray tileMatrixJson = jsonObject.getAsJsonArray("tiles");
        Tile[][] tiles = new Tile[tileMatrixJson.size()][tileMatrixJson.size()];

        int counter1 = 0;
        for (JsonElement tileRowJson : tileMatrixJson) {

            int counter2 = 0;
            for (JsonElement tileJson : tileRowJson.getAsJsonArray()) {
                JsonObject tileJsonObject = tileJson.getAsJsonObject();

                try {
                    tiles[counter1][counter2] = deserializeTile(tileJsonObject);
                } catch (TileTypeNotFoundException t) {
                    System.out.println("Did not recognize that tile type");
                }

                counter2++;
            }
            counter1++;
        }

        return GameModel.fromSave(
                levelId,
                tiles,
                remainingTreasures,
                position,
                keys,
                List.of()
        );
    }

    /**
     * Method to turn a Tile JSON into a Tile Object
     *
     * @param tileJsonObject the Tile JSON
     * @return Tile Object
     * @throws TileTypeNotFoundException could not find the Tile type
     */
    public static Tile deserializeTile(JsonObject tileJsonObject)
            throws TileTypeNotFoundException {
        String tileType = tileJsonObject.get("tileType").getAsString();

        if (tileType.equals("ExitLockTile")) {
            return new ExitLockTile();
        } else if (tileType.equals("ExitTile")) {
            return new ExitTile();
        } else if (tileType.equals("FreeTile")) {
            return new FreeTile();
        } else if (tileType.equals("InfoTile")) {
            String helpMessage = tileJsonObject.get("helpMessage").getAsString();
            return new InfoTile(helpMessage);
        } else if (tileType.equals("KeyTile")) {
            Color colour = new Color(tileJsonObject.get("colour").getAsInt());
            return new KeyTile(colour);
        } else if (tileType.equals("LockDoorTile")) {
            Color colour = new Color(tileJsonObject.get("colour").getAsInt());
            return new LockedDoorTile(colour);
        } else if (tileType.equals("TreasureTile")) {
            return new TreasureTile();
        } else if (tileType.equals("WallTile")) {
            return new WallTile();
        }

        throw new TileTypeNotFoundException("Could not deserialize that Tile Type");
    }
}
