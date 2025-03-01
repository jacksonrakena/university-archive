package nz.ac.wgtn.swen225.lc.app.views;

import nz.ac.wgtn.swen225.lc.app.LarryCroftsAdventures;
import nz.ac.wgtn.swen225.lc.app.input.GameKeyListener;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * The view which displays when the player loses, and the timer gets to 0 on a given level
 *
 * @author renesharma - 300610529
 */
public class FailView extends JPanel {

    /**
     *
     * @param level the level which the player failed on, used to load a new game
     */
    public FailView(int level) {
        setSize(new Dimension(720, 720));
        setLayout(new BorderLayout());
        JLabel failure = new JLabel("<html>You have failed, time has run out!<br>Would you like to try again or exit?<html>");
        failure.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0));
        failure.setHorizontalAlignment(SwingConstants.CENTER);
        failure.setBackground(Color.GRAY);
        failure.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        this.add(failure, BorderLayout.PAGE_START);

        JButton restart = new JButton("Restart level");
        JButton exit = new JButton("Exit Game");
        restart.setPreferredSize(new Dimension(200,100));
        exit.setPreferredSize(new Dimension(200,100));
        restart.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        exit.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.setLayout(new FlowLayout(1, 20, 0));
        buttonPanel.add(restart);
        buttonPanel.add(exit);
        this.add(buttonPanel);

        restart.addActionListener(e -> new LarryCroftsAdventures(GameModel.newGameOnLevel(GameKeyListener.levels[level])));

        exit.addActionListener(e -> System.exit(0));
    }
}
