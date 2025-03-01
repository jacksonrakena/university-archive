package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.recorder.Replay;
import nz.ac.wgtn.swen225.lc.recorder.TileAndLocation;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Class that assists in serializing a Replay
 * @author Tyff Habwe - 300604949
 */
public class ReplaySerializer implements JsonSerializer<Replay> {
    /**
     * Serializes a GameModel to JSON
     *
     * @param replay  the Replay Object to serializer
     * @param type    Object type to change from
     * @param context class to use default serialization tactic
     * @return JsonElement generated
     */
    @Override
    public JsonElement serialize(Replay replay, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        JsonObject treeMap = new JsonObject();

        for (Integer tick : replay.replay().navigableKeySet()) {
            JsonObject tickEventsJson = new JsonObject();
            HashMap<String, Object> tickEvents = replay.replay().get(tick);

            for (String field : tickEvents.keySet()) {
                Object itemStored = tickEvents.get(field);


                if (itemStored instanceof TileAndLocation tandl) {
                    tickEventsJson.add(field,
                            GameModelSerializer.serializeTile(
                                    tandl.tile(),
                                    tandl.position().y(),
                                    tandl.position().x()
                            ));
                    continue;
                }

                if (field.equals("keys")) {
                    List<Color> keys = (List<Color>) itemStored;

                    JsonArray keysJson = new JsonArray();
                    for (Color key : keys) {
                        keysJson.add(key.getRGB());
                    }
                    tickEventsJson.add(field, keysJson);
                    continue;
                }

                tickEventsJson.add(field, context.serialize(itemStored));

            }

            treeMap.add(tick.toString(), tickEventsJson);
        }

        object.add("treeMap", treeMap);
        return object;
    }
}
