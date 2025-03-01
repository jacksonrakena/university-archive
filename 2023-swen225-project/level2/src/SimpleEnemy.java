import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.FreeTile;

import java.util.Arrays;

public class SimpleEnemy extends Agent {
    public SimpleEnemy(Position position) {
        super(position);
    }

    @Override
    public void onTurn(GameModel model) {
        var possibleDirections = Arrays
                .stream(Direction.values())
                .filter(e ->
                model.read(this.position.add(e.getTranslation())) instanceof FreeTile)
                .toList();
        var direction = possibleDirections.get((int) Math.floor(Math.random()*possibleDirections.size()));
        this.direction = direction;
        this.position = this.position.add(direction.getTranslation());
    }
}
