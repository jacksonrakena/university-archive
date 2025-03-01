package nz.ac.wgtn.swen225.lc.recorder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashMap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Color;

import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.DirectionChangeEvent;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.state.TileChangeEvent;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
/**
 * Game Recorder class. Implements PropertyChangeListener and listens for
 * Game State related changes and records them into a Map of Ticks to Changes.
 * Changes are stored as a HashMap of Identifier String to Change Object
 *
 *
 * @author LachlanPC
 *
 */
public class Recorder implements PropertyChangeListener{
	private String savePath = "res/recorder/replays/"; // test implementation, TODO replace with autoincrement later
	private String replayPath;
	private PersistenceManager pManager;
	private GameTimer timer;
	private int lastTick = 0;
	private int tickOffset = 0;
	private TreeMap<Integer, HashMap<String, Object>> stateFrames = new TreeMap<>();
	private GameModel model;

	public Recorder(GameModel model) {
		this.pManager = new PersistenceManager();
		int i = 1;
		while (Files.exists(Paths.get(savePath + Integer.toString(i)))) {
			i++;
		}
		replayPath = savePath + Integer.toString(i);
		System.out.println(replayPath);
		new File(replayPath).mkdirs();
		File stateSave = new File(replayPath + "/state.json");
		this.pManager.save(model, stateSave);
		this.model = model;
		this.timer = new GameTimer();
		stateFrames.put(0, new HashMap<String, Object>());
		model.addPropertyChangeListener(this);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		//get the Current tick
		int tick = timer.getGameTick();
		if (evt instanceof TileChangeEvent tileEvt) {
			// Cast the event into a tileEvent if it is able to be
			Position loc = tileEvt.position();
			//Handle the event.
			handleChange(tick, "tile at: (" + loc.x() + ", " + loc.y() + ") ", new TileAndLocation(loc, model.read(loc)));
			return;
		}
		//If not a tileChangeEvent, Switch on property name.
		switch(evt.getPropertyName()) {
		case "position":
			if (evt instanceof DirectionChangeEvent e) {
				handleChange(tick, "positionAndDirection", new PositionAndDirection((Position)e.getNewValue(), e.direction()));
				return;
			}
			if (!(evt.getNewValue() instanceof Position)) {
				System.err.println("non Position object firing position event");
				return;
			}
			//add the position event to current tick
			handleChange(tick, "position", evt.getNewValue());
			return;
		case "remainingTreasures":
			if (!(evt.getNewValue() instanceof Integer)) {
				System.err.println("non Integer object firing remainingTreasues event");
				System.err.println(evt.getNewValue());
				return;
			}
			//add the remaining treasures event to current tick
			handleChange(tick, "remainingTreasures", evt.getNewValue());
			return;
		case "keys":
			if (!(evt.getNewValue() instanceof Set)) {
				System.err.println("non Set object firing keys event");
				return;
			}
			//Cast the new property to its correct type.
			try {
				@SuppressWarnings("unchecked")
				Set<Color> lc = (Set<Color>) evt.getNewValue();
				//If someone shoves a non Set<Color> object into an event firing keys, I will personally handle it.
				//Add the keys event to current tick as a list (just so it makes a new object each time)
				handleChange(tick, "keys", lc.stream().toList());
			} catch (ClassCastException e) {
				System.err.println("Error Casting keys event to Set<Color>");
				e.printStackTrace();
			}
			return;
		case "tiles":
			System.err.println("FULL TILE ARRAY CHANGED WHILE RECORDING, PLEASE END RECORDING AND REMOVE RECORDER BEFORE LOADING A NEW LEVEL");
			return;
		case "agents":
			handleChange(tick, "agents", evt.getNewValue());
			return;
		case "levelNumber":
			this.saveReplay();
			return;
		default:
			System.err.println("WARN: UNKNOWN PROPERTY CHANGE EVENT FIRED: " + evt.getPropertyName() + "\nChanged from: "
								+ evt.getOldValue() + "\nTo: " + evt.getNewValue());
			return;
		}
	}

	/**
	 * Handles a change event adding it to the Map of tick to events.
	 * Identifier ensures the same tile location or gamestate object
	 * can't update more than once per tick.
	 *
	 * @param tick - Integer tick number event took place on
	 * @param identifier - Unique String identifier for type of/location of update
	 * @param change - Changed Object
	 */
	public void handleChange(int tick, String identifier, Object change) {
		if ((tick - lastTick) > 10 || stateFrames.get(lastTick) == null || stateFrames.get(lastTick).keySet().contains(identifier)) {
			if (stateFrames.get(lastTick).keySet().contains(identifier)) {tickOffset++;}
			lastTick = tick + tickOffset;
			stateFrames.put(lastTick, new HashMap<String, Object>());
		}
		stateFrames.get(lastTick).put(identifier, change);
//		System.out.println(lastTick + ":" + stateFrames.get(lastTick));
	}

	/**
	 * Get current replay.
	 *
	 * @return Replay - Replay record of Tick Map.
	 */
	public Replay getReplay(){
		return new Replay((TreeMap<Integer, HashMap<String,Object>>)stateFrames.clone());
	}

	public String getReplayPath() {
		return replayPath;
	}

	public void saveReplay() {
		pManager.save(getReplay(), new File(getReplayPath() + "/replay.json"));
	}


}
