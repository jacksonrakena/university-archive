package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AgentTests {
    @Test
    public void testAgent() {
        var agent = new Agent(new Position(2, 2)) {
            @Override
            public void onTurn(GameModel model) {

            }
        };
        Assertions.assertEquals(new Position(2,2),agent.position);
    }
}
