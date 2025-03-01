package nz.ac.wgtn.swen225.lc.app.data;

import nz.ac.wgtn.swen225.lc.renderer.MainWindow;
import nz.ac.wgtn.swen225.lc.app.input.GameKeyListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * GameTimer is a timer the counts down from 60 seconds, also has tick system to help inputs
 *
 * @author renesharma - 300610529
 */
public class GameTimer implements ActionListener {

    private final Timer timer = new Timer(Integer.MAX_VALUE, this);
    private GameKeyListener gameKeyListener;
    private MainWindow mainWindow;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public int gameTick = 0;
    private int seconds = 60;

    public GameTimer() {
        timer.setDelay(20);
        timer.setInitialDelay(1000);
        timer.start();
    }

    /**
     * Return the current gameTick
     *
     * @return current game tick
     */
    public int getGameTick() {
        return gameTick;
    }

    /**
     * Return the current second
     *
     * @return current seconds
     */
    public int getSecondsRemaining() {
        return seconds;
    }

    /**
     * Add a game key listener to the timer
     *
     * @param gameKeyListener a game key listener
     */

    public void addGameKeyListener(GameKeyListener gameKeyListener) {
        this.gameKeyListener = gameKeyListener;
    }

    /**
     * add a main window to the timer
     *
     * @param mainWindow mainWindow
     */
    public void addMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Pause the timer
     *
     * @param stopped if the game has stopped
     */
    public void pause(boolean stopped) {
        if (stopped) {
           timer.stop();
        } else {
            timer.start();
        }
    }

    /**
     * resets the clock
     */
    public void resetTime() {
        this.seconds = 60;
    }

    /**
     * Add a property change listener
     *
     * @param listener property change listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Every 20ms this method is run, and will increment the game ticks and decide if a second has passed
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int oldSeconds = seconds;
        gameTick++;
        if (gameKeyListener != null) {
            this.gameKeyListener.onKeyPress(1000 / timer.getDelay(), true);
        }

        if ((timer.getDelay()*gameTick)%1000 == 0) {
            seconds--;
        }

        if (seconds == 0) {
            if (mainWindow != null) {
                mainWindow.setFail();
            }
            timer.stop();
        }
        propertyChangeSupport.firePropertyChange("seconds", oldSeconds, seconds);
    }
}
