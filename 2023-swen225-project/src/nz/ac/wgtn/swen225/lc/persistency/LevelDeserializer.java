package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;
import nz.ac.wgtn.swen225.lc.persistency.exceptions.TileTypeNotFoundException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Class that assists in deserializing a Level
 * @author Tyff Habwe - 300604949
 */
public class LevelDeserializer implements JsonDeserializer<Level> {
    /**
     * Deserializes a GameModel from JSON
     * @param jsonElement a java abstraction of the JSON file
     * @param type Object type to change to
     * @param context class to use default deserialization tactic
     * @return GameModel generated
     * @throws JsonParseException if something went wrong
     */
    @Override
    public Level deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();

        String levelId = object.get("mapId").getAsString();
        Position position = context.deserialize(object.get("position"), Position.class);

        JsonArray tileMatrixJson = object.getAsJsonArray("tiles");
        Tile[][] tiles = new Tile[tileMatrixJson.size()][tileMatrixJson.size()];

        int counter1 = 0;
        for (JsonElement tileRowJson : tileMatrixJson) {

            int counter2 = 0;
            for (JsonElement tileJson : tileRowJson.getAsJsonArray()) {
                JsonObject tileJsonObject = tileJson.getAsJsonObject();

                try {
                    tiles[counter1][counter2] = GameModelDeserializer.deserializeTile(tileJsonObject);
                } catch (TileTypeNotFoundException t) {
                    System.out.println("Did not recognize that tile type");
                }

                counter2++;
            }
            counter1++;
        }
        return new Level(levelId, tiles, position, List.of());
    }
}
