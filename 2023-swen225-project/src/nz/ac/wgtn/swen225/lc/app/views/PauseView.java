package nz.ac.wgtn.swen225.lc.app.views;

import javax.swing.*;
import java.awt.*;

/**
 * The view that will be display when the game is paused
 *
 * @author renesharma - 300610529
 */
public class PauseView extends JPanel {

    public PauseView() {
        setSize(new Dimension(720, 720));
        setLayout(new BorderLayout());
        JLabel paused = new JLabel("Game is paused");
        paused.setHorizontalAlignment(SwingConstants.CENTER);
        paused.setBackground(Color.GRAY);
        paused.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 70));
        this.add(paused, BorderLayout.CENTER);
    }
}
