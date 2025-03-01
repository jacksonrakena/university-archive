package nz.ac.wgtn.swen225.lc.app.views;

import nz.ac.wgtn.swen225.lc.app.CustomButton;
import nz.ac.wgtn.swen225.lc.app.FileSelector;
import nz.ac.wgtn.swen225.lc.app.LarryCroftsAdventures;
import nz.ac.wgtn.swen225.lc.app.data.GameTimer;
import nz.ac.wgtn.swen225.lc.app.views.panels.KeysPanel;
import nz.ac.wgtn.swen225.lc.app.views.panels.OtherPanel;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.persistency.PersistenceManager;
import nz.ac.wgtn.swen225.lc.recorder.Recorder;
import nz.ac.wgtn.swen225.lc.recorder.Replay;
import nz.ac.wgtn.swen225.lc.recorder.ReplayPlayer;
import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The window to the right of the board, which displays information based on the current GameModel
 *
 * @author renesharma - 300610529
 */
public class ApplicationWindow extends JPanel {

    private final String savePath = "/res/app/saves/save";

    private final Color backgroundColor = new Color(105,148,179);
    private final GameTimer clock = new GameTimer();
    private final JPanel buttonContainer = new JPanel();
    private final JPanel panelContainer = new JPanel();
    private ReplayPlayer replayPlayer;
    private Replay replay;
    private boolean isRecording = false;
    private int sliderValue = 0;
    private Recorder recorder;
    private final GameModel model;
    private final MainWindow mw;
    private KeysPanel keysPanel;
    private OtherPanel timePanel;
    private OtherPanel levelPanel;
    private OtherPanel treasurePanel;
    private CustomButton recording;
    private JPanel sliderContainer;
    private JSlider slider;

    /**
     * Initialise a new Application Window
     *
     * @param model the current state of the game in which information is pulled from
     * @param mw the parent of the app window
     */

    public ApplicationWindow(GameModel model, MainWindow mw) {

        this.model = model;
        this.mw = mw;
        this.clock.addMainWindow(mw);

        this.setSize(new Dimension(350, 720));
        this.setBackground(backgroundColor);

        createButtons();
        createSlider();
        createPanels();

        this.add(buttonContainer);
        this.add(sliderContainer);
        this.add(panelContainer);
    }

    /**
     * Create the four buttons, with different Action Listeners and names
     */
    private void createButtons() {

        buttonContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonContainer.setLayout(new GridLayout(5, 2, 5, 5));
        buttonContainer.setBackground(backgroundColor);

        CustomButton htp = new CustomButton("How to play");
        htp.addActionListener(e -> new RuleView(mw));
        buttonContainer.add(htp);

        CustomButton htr = new CustomButton("Recorder guide");
        htr.addActionListener(e -> new RecorderView(mw));
        buttonContainer.add(htr);


        CustomButton quit = new CustomButton("Quit Game");
        quit.addActionListener(e -> System.exit(0));
        buttonContainer.add(quit);


        CustomButton save = new CustomButton("Make a save");
        save.addActionListener(e -> {
            int i = 1;
            while (Files.exists(Paths.get(System.getProperty("user.dir") + savePath + Integer.toString(i) + ".json"))) {
                i++;
            }
            String toSave = savePath + Integer.toString(i) + ".json";
            File saveFile = new File(System.getProperty("user.dir") + toSave);
            new PersistenceManager().save(model, saveFile);
        });
        buttonContainer.add(save);


        CustomButton load = new CustomButton("Load a save");
        load.addActionListener(e -> openFileChooser(false));
        buttonContainer.add(load);

        createRecordButton("start recording");

        CustomButton startReplay = new CustomButton("start replay");
        startReplay.addActionListener(e -> {
            if (replay != null) {
                replayPlayer = new ReplayPlayer(model, replay.replay());
                if (sliderValue == 0) {
                    replayPlayer.pause();
                } else if(sliderValue == 100) {
                    replayPlayer.realTime();
                    replayPlayer.play();
                } else {
                    replayPlayer.setTime(sliderValue);
                    replayPlayer.play();
                }
                replayPlayer.play();
            }
        });
        buttonContainer.add(startReplay);

        CustomButton oneStep = new CustomButton("one step replay");
        oneStep.addActionListener(e -> {
            if (replayPlayer != null) {
                replayPlayer.stepByStep();
            }
        });
        buttonContainer.add(oneStep);

        CustomButton stopReplay = new CustomButton("pause replay");
        stopReplay.addActionListener(e -> {
            if (replayPlayer != null) {
                replayPlayer.pause();
            }
        });
        buttonContainer.add(stopReplay);

        CustomButton loadReplay = new CustomButton("load replay");
        loadReplay.addActionListener(e -> openFileChooser(true));
        buttonContainer.add(loadReplay);
    }

    /**
     * Create a record button with an ActionListener
     */
    private void createRecordButton(String text) {

        recording = new CustomButton(text);
        recording.addActionListener(e -> {
            isRecording = !isRecording;
            toggleRecorderText();

            if (isRecording) {
                recorder = new Recorder(model);
            } else {
                replay = recorder.getReplay();
                recorder.saveReplay();
            }
        });
        buttonContainer.add(recording, 5);
    }

    /**
     * Creates the slider with its title
     */
    private void createSlider() {

        sliderContainer = new JPanel();
        sliderContainer.setBackground(backgroundColor);
        sliderContainer.setLayout(new GridLayout(2, 1, 0, 5));
        JLabel label = new JLabel();
        label.setText("Speed of replay");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        sliderContainer.add(label);

        slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slider.setMinorTickSpacing(10);
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setLabelTable(slider.createStandardLabels(20));
        slider.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        slider.setFocusable(false);
        sliderContainer.add(slider);

        slider.addChangeListener(e -> sliderValue = slider.getValue());
    }

    /**
     * Change the text on the recorder button
     */
    private void toggleRecorderText() {

        buttonContainer.remove(recording);
        if (isRecording) {
            createRecordButton("stop recording");
        } else {
            createRecordButton("start recording");
        }
        buttonContainer.validate();
    }

    /**
     * Creates all four panels
     */
    private void createPanels() {

        panelContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelContainer.setLayout(new GridLayout(4, 1, 0, 5));
        panelContainer.setBackground(backgroundColor);

        timePanel = new OtherPanel("Time Remaining", String.valueOf(getTimer().getSecondsRemaining()));
        panelContainer.add(timePanel, 0);
        levelPanel = new OtherPanel("Level", String.valueOf(model.levelNumber() + 1));
        panelContainer.add(levelPanel, 1);
        treasurePanel = new OtherPanel("Treasures Left", String.valueOf(model.remainingTreasures()));
        panelContainer.add(treasurePanel, 2);
        keysPanel = new KeysPanel("Keys", model);
        panelContainer.add(keysPanel, 3);

        this.setVisible(true);

        clock.addPropertyChangeListener(evt -> {
            if ("seconds".equals(evt.getPropertyName())) {
                int newValue = (int) evt.getNewValue();
                panelContainer.remove(timePanel);
                timePanel = new OtherPanel("Time Remaining", String.valueOf(newValue));
                panelContainer.add(timePanel, 0);
                panelContainer.validate();
            }
        });

        model.addPropertyChangeListener(evt -> {
            if ("levelNumber".equals(evt.getPropertyName())) {
                int newValue = (int) evt.getNewValue();
                panelContainer.remove(levelPanel);
                levelPanel = new OtherPanel("Level", String.valueOf(newValue + 1));
                panelContainer.add(levelPanel, 1);
                panelContainer.validate();
                clock.resetTime();
            }
        });

        model.addPropertyChangeListener(evt -> {
            if ("remainingTreasures".equals(evt.getPropertyName())) {
                int newValue = (int) evt.getNewValue();
                panelContainer.remove(treasurePanel);
                if (newValue == 0) {
                    treasurePanel = new OtherPanel("Treasures Left", "get to the exit now!");
                    treasurePanel.changeFontSize(20);
                } else {
                    treasurePanel = new OtherPanel("Treasures Left", String.valueOf(model.remainingTreasures()));
                }
                panelContainer.add(treasurePanel, 2);
                panelContainer.validate();
            }
        });

        model.addPropertyChangeListener(evt -> {
            if ("keys".equals(evt.getPropertyName())) {
                panelContainer.remove(keysPanel);
                keysPanel = new KeysPanel("Keys", model);
                panelContainer.add(keysPanel, 3);
                panelContainer.validate();
            }
        });
    }

    /**
     * Opens a new FileChooser that will be in directory mode or file mode depending on the boolean loadReplay
     *
     * @param loadReplay to decide whether the file chooser will be in loading replay mode
     */
    private void openFileChooser(boolean loadReplay) {

        mw.getApplicationWindow().getTimer().pause(true);
        mw.setPaused(true);
        FileSelector fileSelector = new FileSelector(mw, loadReplay);
        int returnValue = fileSelector.getFileChooser().showOpenDialog(null);

        try {
            switch (returnValue) {
                case JFileChooser.APPROVE_OPTION -> {
                    if (loadReplay) {
                        mw.getApplicationWindow().getTimer().pause(false);
                        mw.setPaused(false);
                        File replayFile = new File(fileSelector.getFileChooser().getSelectedFile().getAbsolutePath() + "/replay.json" );
                        File gameModelFile = new File(fileSelector.getFileChooser().getSelectedFile().getAbsolutePath() + "/state.json" );
                        GameModel newGame = null;
                        Replay replay = null;
                        try {
                            newGame = new PersistenceManager().loadGameModel(gameModelFile);
                            replay = new PersistenceManager().loadReplay(replayFile);
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        new LarryCroftsAdventures(newGame, replay);
                    } else {
                        mw.getApplicationWindow().getTimer().pause(false);
                        mw.setPaused(false);
                        File selectedFile = fileSelector.getFileChooser().getSelectedFile();
                        GameModel newGame = new PersistenceManager().loadGameModel(selectedFile);
                        new LarryCroftsAdventures(newGame);
                    }
                }
                case JFileChooser.CANCEL_OPTION -> {
                    System.out.println("No file selected");
                    mw.getApplicationWindow().getTimer().pause(false);
                    mw.setPaused(false);
                }

                case JFileChooser.ERROR_OPTION -> {
                    System.out.println("Error selecting file");
                    mw.getApplicationWindow().getTimer().pause(false);
                    mw.setPaused(false);
                }
            }
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Loads a replay into the application window that will be ready to be played
     *
     * @param replay the replay that will be loaded into the game
     */
    public void setReplay(Replay replay) {
        this.replay = replay;
    }
    
    /**
     * returns the clock
     */
    public GameTimer getTimer() {
        return clock;
    }
}
