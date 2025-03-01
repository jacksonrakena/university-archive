package nz.ac.wgtn.swen225.lc.renderer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * The SoundControl class manages playing audio in the game.
 * @author burkitkahu - 300604675
 *
 */
public class SoundControl {
    Clip clip;
    Clip mainSong;
    public enum SoundList {mainSong, walking, pickup}
    HashMap<SoundList, URL> sounds = new HashMap<>();

    /**
     * Constructs the SoundControl and initializes the sound resources.
     */
    public SoundControl(){
        sounds.put(SoundList.mainSong, getClass().getResource("/renderer/sounds/amongusSongRemix.wav"));
        sounds.put(SoundList.walking, getClass().getResource("/renderer/sounds/amongUsWalking.wav"));
        sounds.put(SoundList.pickup, getClass().getResource("/renderer/sounds/itemEquip.wav"));
    }

    /**
     * Plays the main game song.
     */
    public void playSong(){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sounds.get(SoundList.mainSong));
            mainSong = AudioSystem.getClip();
            mainSong.open(ais);
        }catch(IOException | LineUnavailableException e){
            System.err.println("Error playing audio");
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
        mainSong.loop(Clip.LOOP_CONTINUOUSLY);
        mainSong.start();
    }

    /**
     *  Stops the background music
     */
    public void stopSong(){
        mainSong.stop();
    }

    /**
     * Plays a specific sound from the available sound options.
     *
     * @param toPlay The sound to be played from the soundList enum.
     */
    public void playSound(SoundList toPlay){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sounds.get(toPlay));
            clip = AudioSystem.getClip();
            clip.open(ais);
        }catch(IOException | LineUnavailableException e){
            System.err.println("Error playing audio");
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
        play();
    }

    /**
     * Starts playing the audio.
     */
    public void play(){
        clip.start();
    }
}
