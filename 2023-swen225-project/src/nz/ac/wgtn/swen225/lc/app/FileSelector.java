package nz.ac.wgtn.swen225.lc.app;

import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Selector that pops up when you want to load a save
 *
 * @author renesharma - 300610529
 */
public class FileSelector extends JFrame {

    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Initialise a new FileSelector
     *
     * @param mw the parent window in which it will be situated
     * @param loadReplay boolean dictating whether the file chooser is in directory mode or not to load a replay
     */
    public FileSelector(MainWindow mw, boolean loadReplay) {
        if (loadReplay) {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/res/recorder/replays/"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/res/app/saves/"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("json files/game saves", "json");
            fileChooser.setFileFilter(filter);
        }

        mw.getApplicationWindow().getTimer().pause(true);
        mw.setPaused(true);

        this.setContentPane(fileChooser);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mw.getApplicationWindow().getTimer().pause(false);
                mw.setPaused(false);
                FileSelector.this.dispose();
            }
        });
    }

    /**
     * Returns the File Chooser which should be immutable
     *
     * @return fileChooser
     */
    public JFileChooser getFileChooser() {
        return fileChooser;
    }
}
