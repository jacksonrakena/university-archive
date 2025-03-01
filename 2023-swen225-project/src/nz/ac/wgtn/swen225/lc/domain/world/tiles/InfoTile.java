package nz.ac.wgtn.swen225.lc.domain.world.tiles;

import nz.ac.wgtn.swen225.lc.domain.Controller;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.world.abs.MovableTile;
import nz.ac.wgtn.swen225.lc.domain.world.abs.Tile;

/**
 * Represents an information tile, which communicates information to the player when they walk over it.
 */
public class InfoTile extends MovableTile {
    private String helpMessage;

    public String helpMessage() { return this.helpMessage; }

    public InfoTile(String help) {
        this.helpMessage = help;
    }

    @Override
    public void onMoveInto(Position self, Controller controller) {
        controller.model().setHelpMessage(this.helpMessage);
    }

    @Override
    public Tile clone() {
        return new InfoTile(helpMessage);
    }
}
