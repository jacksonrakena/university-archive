package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.app.views.ApplicationWindow;
import nz.ac.wgtn.swen225.lc.app.views.FailView;
import nz.ac.wgtn.swen225.lc.app.views.PauseView;
import nz.ac.wgtn.swen225.lc.app.views.WinView;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * The MainWindow class represents the main game window where the game is displayed.
 *
 * @author burkitkahu - 300604675
 */
public class MainWindow extends JFrame {

    private final ApplicationWindow applicationWindow;
    private final BoardView boardView;
    private final PauseView pauseView;
    private final WinView winView;
    private final FailView failView;

    /**
     *
     * @param model The GameModel instance that provides the game state.
     */
    public MainWindow(GameModel model) {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setSize(1320, 900);
        this.getContentPane().setBackground(Color.YELLOW);
        this.getContentPane().requestFocusInWindow();

        // Size and position of the board view
        boardView = new BoardView(model);
        boardView.setLocation(70, 60);
        this.getContentPane().add(boardView);

        pauseView = new PauseView();
        pauseView.setLocation(70, 60);
        pauseView.setVisible(false);
        this.getContentPane().add(pauseView);

        winView = new WinView();
        winView.setLocation(70, 60);
        winView.setVisible(false);
        this.getContentPane().add(winView);

        failView = new FailView(model.levelNumber());
        failView.setLocation(70, 60);
        failView.setVisible(false);
        this.getContentPane().add(failView);

        applicationWindow = new ApplicationWindow(model, this);
        applicationWindow.setLocation(880, 60);
        this.getContentPane().add(applicationWindow);

        try {
            ImagePanel test = new ImagePanel(ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/background.png"))));
            test.setBounds(0, 0, 1320, 900);
            this.add(test);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        this.getContentPane().validate();
        this.setVisible(true);
    }

    /**
     * Gets the application window.
     *
     * @return The ApplicationWindow instance.
     */
    public ApplicationWindow getApplicationWindow() {
        return applicationWindow;
    }

    /**
     * Sets the game state to paused or resumed.
     *
     * @param paused true if the game is paused; false if it's resumed.
     */
    public void setPaused(boolean paused) {
        if (paused) {
            pauseView.setVisible(true);
            boardView.setVisible(false);
        } else {
            pauseView.setVisible(false);
            boardView.setVisible(true);
        }
    }

    /**
     * Sets the game state to "fail" view.
     */
    public void setFail() {
        pauseView.setVisible(false);
        boardView.setVisible(false);
        failView.setVisible(true);
    }

    /**
     * returns the board view
     * @return board view
     */
    public BoardView getBoardView(){return this.boardView;}

    /**
     * Sets the game state to "win" view.
     */
    public void setWinView() {
        pauseView.setVisible(false);
        boardView.setVisible(false);
        failView.setVisible(false);
        winView.setVisible(true);
    }
}
