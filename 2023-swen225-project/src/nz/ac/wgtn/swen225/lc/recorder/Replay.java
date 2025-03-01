package nz.ac.wgtn.swen225.lc.recorder;

import java.util.HashMap;
import java.util.TreeMap;
/**
 * Record containing the replay Map to assist with serialization.
 * 
 * @author LachlanPC
 *
 */
public record Replay(TreeMap<Integer, HashMap<String, Object>> replay) {
	
}