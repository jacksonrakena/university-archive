package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.persistency.exceptions.TileTypeNotFoundException;
import nz.ac.wgtn.swen225.lc.recorder.PositionAndDirection;
import nz.ac.wgtn.swen225.lc.recorder.Replay;
import nz.ac.wgtn.swen225.lc.recorder.TileAndLocation;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Class that assists in deserializing a Replay
 * @author Tyff Habwe - 300604949
 */
public class ReplayDeserializer implements JsonDeserializer<Replay> {
    /**
     * Deserializes a GameModel from JSON
     *
     * @param jsonElement a java abstraction of the JSON file
     * @param type        Object type to change to
     * @param context     class to use default deserialization tactic
     * @return GameModel generated
     * @throws JsonParseException if something went wrong
     */
    @Override
    public Replay deserialize(JsonElement jsonElement, Type type,
                              JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonObject treeMapJson = jsonObject.getAsJsonObject("treeMap");

        TreeMap<Integer, HashMap<String, Object>> replayMap = new TreeMap<>();

        for (String tickString : treeMapJson.keySet()) {
            int tick = Integer.parseInt(tickString);
            JsonObject tickEventsJson = treeMapJson.getAsJsonObject(tickString);
            HashMap<String, Object> tickEvents = new HashMap<>();

            for (String field : tickEventsJson.keySet()) {
                JsonElement fieldElement = tickEventsJson.get(field);
                Object fieldValue = null;

                // Deserialize based on the field type
                if (field.equals("keys")) {
                    // Handle deserialization of keys (List<Color>)
                    fieldValue = deserializeKeys(fieldElement.getAsJsonArray());
                }
                if (field.contains("tile")) {
                    // Deserialize Tile
                    try {
                        JsonObject tileAndPos = (JsonObject) tickEventsJson.get(field);
                        Tile t = GameModelDeserializer.deserializeTile(fieldElement.getAsJsonObject());
                        int x = tileAndPos.get("x").getAsInt();
                        int y = tileAndPos.get("y").getAsInt();
                        var tandL = new TileAndLocation(new Position(x, y), t);

                        fieldValue = tandL;
                    } catch (TileTypeNotFoundException ignored) {
                    }
                }
                if (field.equals("position")) {
                    fieldValue = context.deserialize(fieldElement, Position.class);
                }

                if (field.equals("positionAndDirection")) {
                    JsonObject posAndDJSON = (JsonObject) tickEventsJson.get(field);
                    Position pos = context.deserialize(posAndDJSON.get("position"), Position.class);
                    Direction dir = context.deserialize(posAndDJSON.get("direction"), Direction.class);

                    fieldValue = new PositionAndDirection(pos, dir);
                }
                if(field.equals("agents")) {
                    JsonArray agents = (JsonArray) tickEventsJson.get(field);
                    List<Agent> agentsList = new ArrayList<>();

                    for(JsonElement posAndDir: agents) {
                        JsonObject posAndD = (JsonObject) posAndDir;
                        Position p = context.deserialize(posAndD.get("position"), Position.class);
                        Direction dir = context.deserialize(posAndD.get("direction"), Direction.class);

                        Agent theAgent = new PersistenceManager().getSimpleEnemy(p);
                        theAgent.direction = dir;

                        agentsList.add(theAgent);
                    }

                    fieldValue = agentsList;
                }

                tickEvents.put(field, fieldValue);
            }
            replayMap.put(tick, tickEvents);
        }

        return new Replay(replayMap);
    }


    /**
     * Helper method to deserialize keys of Json
     * @param keysJson the json representation of keys (int RBG colours)
     * @return a list of colours that represents keys in current game
     */
    private List<Color> deserializeKeys(JsonArray keysJson) {
        List<Color> keys = new ArrayList<>();
        for (JsonElement colorElement : keysJson) {
            int colorRGB = colorElement.getAsInt();
            keys.add(new Color(colorRGB));
        }
        return keys;
    }
}
