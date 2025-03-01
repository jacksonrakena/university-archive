package nz.ac.wgtn.swen225.lc.persistency;

import com.google.gson.*;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;

import java.awt.*;
import java.lang.reflect.Type;
/**
 * Class that assists in serializing a GameModel
 * @author Tyff Habwe - 300604949
 */
public class GameModelSerializer implements JsonSerializer<GameModel> {
    /**
     * Serializes a GameModel to JSON
     * @param model the GameModel Object to serializer
     * @param type Object type to change from
     * @param jsonSerializationContext class to use default serialization tactic
     * @return JsonElement generated
     */
    @Override
    public JsonElement serialize(GameModel model, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        object.addProperty("levelId", model.levelId());


        object.addProperty("remainingTreasures", model.remainingTreasures());

        object.add("position", jsonSerializationContext.serialize(model.position()));

        JsonArray keys = new JsonArray();
        model.keys().stream().map(Color::getRGB).forEach(keys::add);
        object.add("keys", keys);

        JsonArray tileMatrix = new JsonArray();

        int counter1 = 0;
        for(Tile[] tileRow: model.tiles()) {
            JsonArray tileRowArray = new JsonArray();

            int counter2 = 0;
            for(Tile tile: tileRow) {

                tileRowArray.add(serializeTile(tile, counter1, counter2));
                counter2++;
            }

            tileMatrix.add(tileRowArray);
            counter1++;
        }

        object.add("tiles", tileMatrix);
        return object;
    }
    /**
     * Take a Tile and turn it into a JsonObject
     * @param t The Tile to serialize
     * @param row row to add
     * @param column column to add
     * @return JsonObject of the Tile data
     */
    public static JsonObject serializeTile(Tile t, int row, int column) {
        JsonObject tileObject = new JsonObject();

        if(t instanceof ExitLockTile) {
            tileObject.addProperty("tileType", "ExitLockTile");
        }
        else if(t instanceof ExitTile) {
            tileObject.addProperty("tileType", "ExitTile");
        }
        else if(t instanceof FreeTile) {
            tileObject.addProperty("tileType", "FreeTile");
        }
        else if(t instanceof InfoTile infoTile) {
            tileObject.addProperty("tileType", "InfoTile");
            tileObject.addProperty("helpMessage", infoTile.helpMessage());
        }
        else if (t instanceof KeyTile keyTile) {
            tileObject.addProperty("tileType", "KeyTile");
            tileObject.addProperty("colour", keyTile.getColour().getRGB());
        }
        else if (t instanceof LockedDoorTile lockedDoorTile) {
            tileObject.addProperty("tileType", "LockDoorTile");
            tileObject.addProperty("colour", lockedDoorTile.getColour().getRGB());
        }
        else if(t instanceof TreasureTile) {
            tileObject.addProperty("tileType", "TreasureTile");
        }
        else if(t instanceof WallTile) {
            tileObject.addProperty("tileType", "WallTile");
        }
        else {
            tileObject.addProperty("tileType", "UNKNOWN_TILE_TYPE");
        }

        tileObject.addProperty("x", column);
        tileObject.addProperty("y", row);
        return tileObject;
    }
}


