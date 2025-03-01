package nz.ac.wgtn.swen225.lc.app.views;

import javax.swing.*;
import java.awt.*;

/**
 * The view that will be displayed once the game has ended, and you have beaten the game
 *
 * @author renesharma - 300610529
 */
public class WinView extends JPanel {

    public WinView() {
        setSize(new Dimension(720, 720));
        this.setLayout(new BorderLayout());

        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(300,0,0,0));

        JLabel paused = new JLabel("Congratulations, You have Won!");
        paused.setHorizontalAlignment(SwingConstants.CENTER);
        paused.setBackground(Color.GRAY);
        paused.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));

        JButton exit = new JButton("exit game");
        exit.addActionListener(e -> System.exit(0));

        container.add(paused);
        container.add(exit);

        this.add(container, BorderLayout.CENTER);
    }
}
