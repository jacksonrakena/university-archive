package nz.ac.wgtn.swen225.lc.recorder;

import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.abs.agents.Agent;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.KeyTile;

import java.util.TreeMap;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import javax.swing.Timer;

/**
 * Replay Player Class. Takes an initial state game mode, as well as a Map of all
 * ticks and changes then replays those events, updating the model with each change.
 *
 * @author LachlanPC
 *
 */
public class ReplayPlayer {


	GameModel model;
	TreeMap<Integer, HashMap<String, Object>> replay;
	Timer timer;
	boolean isPlaying = true;
	boolean isRealTime = true;
	private Iterator<Integer> iterator;
	int gameTick = 0;
	int lastTick = 0;
	private boolean hasEnded = false;

	public ReplayPlayer(GameModel model, TreeMap<Integer, HashMap<String, Object>> replay) {
		this.model = model;
		this.replay = replay;
		iterator = replay.navigableKeySet().iterator();
		System.out.println(replay);
		timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRealTime) {
                	replayRealTime();
                	gameTick++;
                } else {
                	replaySetTime();
                }
            }
        });
	}

	private void replayRealTime() {
		if (!iterator.hasNext()) {
        	this.hasEnded = true;
        	this.timer.stop();
        }
        int currentTime = this.gameTick + 1;
        do {
            if (lastTick <= currentTime && replay.keySet().contains(lastTick)) {
            	HashMap<String, Object> tick = replay.get(lastTick);
                for (String s: tick.keySet()) {
                	processEvent(s, tick.get(s));
                }
                if (iterator.hasNext()) {
                	lastTick = iterator.next();
                }

            } else if (lastTick > currentTime) {
            	break;
            }
        }while (iterator.hasNext() && lastTick <= currentTime);

	}

	private void replaySetTime() {
		System.out.println(iterator.hasNext());
		if (iterator.hasNext()) {
            gameTick = iterator.next();
            HashMap<String, Object> tick = replay.get(gameTick);
            for (String s: tick.keySet()) {
            	processEvent(s, tick.get(s));
            }
        } else {
        	this.hasEnded  = true;
        	this.timer.stop();
        }
	}


	private void processEvent(String s, Object o) {
		if (o instanceof TileAndLocation) {
			TileAndLocation tl = (TileAndLocation) o;
			model.write(tl.position(), tl.tile());
			if (tl.tile() instanceof KeyTile kt) {
				System.err.println(kt.getColour());
			}
			return;
		}
		switch(s) {
		case "positionAndDirection":
			if (o instanceof PositionAndDirection pd) {
				model.position(pd.position(), pd.direction());
			}
			return;
		case "position":
			model.position((Position) o, Direction.UP);
			return;

		case "keys":
			Set<Color> newKeys = new HashSet<>();
			if (o instanceof List l) {
				newKeys.addAll(l);
				model.setKeys(newKeys);
			}
			return;

		case "remainingTreasures":
			model.decrementRemainingTreasures();
			return;

		case "agents":
			if (o instanceof List) {
				List<Agent> dummyList = new ArrayList<>();
				for (Agent a: (List<Agent>) o) {
					DummyEnemy newAgent = new DummyEnemy(a.position, a.direction);
					dummyList.add(newAgent);
				}
				model.setAgents(dummyList);
			}
			
			return;
		default:
			return;
		}

	}

	public void setTime(int time) {
		timer.setDelay(time);
		isRealTime = false;
	}

	public void realTime() {
		timer.setDelay(20);
		isRealTime = true;
	}

	public void stepByStep() {
		isRealTime = false;
		timer.setDelay(Integer.MAX_VALUE);
		replaySetTime();
	}

	public void play() {
		timer.start();
	}

	public void pause() {
		timer.stop();
	}

	public boolean hasEnded() {
		return this.hasEnded;
	}

	public static boolean findValidReplay(String pathString) {
		if (!Files.exists(Paths.get(pathString + "/replay.json"))) {
			return false;
		}
		if (!Files.exists(Paths.get(pathString + "/state.json"))) {
			return false;
		}
		return true;
	}



}
