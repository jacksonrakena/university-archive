package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.level.Level;

import java.lang.reflect.Type;
/**
 * Class that assists in serializing a Level
 * @author Tyff Habwe - 300604949
 */
public class LevelSerializer implements JsonSerializer<Level> {
    /**
     * Serializes a GameModel to JSON
     * @param level the Level Object to serializer
     * @param type Object type to change from
     * @param context class to use default serialization tactic
     * @return JsonElement generated
     */
    @Override
    public JsonElement serialize(Level level, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("mapId", level.mapId());
        object.add("position", context.serialize(level.playerSpawnPosition()));

        JsonArray tileMatrix = new JsonArray();

        int counter1 = 0;
        for(Tile[] tileRow: level.originalTiles()) {

            JsonArray tileRowArray = new JsonArray();

            int counter2 = 0;
            for(Tile tile: tileRow) {
                JsonObject tileAsAJsonObject = GameModelSerializer.serializeTile(tile, counter1, counter2);
                tileRowArray.add(tileAsAJsonObject);
                counter2++;
            }

            tileMatrix.add(tileRowArray);
            counter1++;
        }

        object.add("tiles", tileMatrix);
        return object;
    }
}
