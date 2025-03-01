package nz.ac.wgtn.swen225.lc.app.views;

import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Rules of the game, which will be displayed in its own external view
 *
 * @author renesharma - 300610529
 */
public class RuleView extends JFrame {

    /**
     *
     * @param mw the parent window
     */
    public RuleView(MainWindow mw) {

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(900, 500));
        this.setBackground(Color.GRAY);

        JPanel container = new JPanel();
        container.setLayout(new GridLayout(5, 1));

        JLabel title = new JLabel(),
                step1 = new JLabel(),
                step2 = new JLabel(),
                step3 = new JLabel(),
                step4 = new JLabel();

        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        title.setFont(labelFont);
        step1.setFont(labelFont);
        step2.setFont(labelFont);
        step3.setFont(labelFont);
        step4.setFont(labelFont);

        title.setText("     How to play");
        step1.setText("  1.   Collect keys to enter the doors with the corresponding key color");
        step2.setText("  2.   Collect all the treasures around the map whilst avoiding enemies");
        step3.setText("  3.   Avoid enemies by standing still and hiding, they will walk right past you");
        step4.setText("  4.   After collecting all the treasures, get to the exit before the time runs out");

        container.add(title);
        title.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(step1);
        step1.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(step2);
        step2.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(step3);
        step3.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(step4);
        step4.setHorizontalAlignment(SwingConstants.LEFT);

        mw.getApplicationWindow().getTimer().pause(true);
        mw.setPaused(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                mw.getApplicationWindow().getTimer().pause(false);
                mw.setPaused(false);
            }
        });

        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                mw.getApplicationWindow().getTimer().pause(true);
                mw.setPaused(true);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                mw.getApplicationWindow().getTimer().pause(false);
                mw.setPaused(false);
                RuleView.this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });

        this.add(container);
        setVisible(true);
    }
}
