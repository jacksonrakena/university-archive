package nz.ac.wgtn.swen225.lc.app.views;

import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * View the show how to use the recorder functionality
 *
 * @author renesharma - 300610529
 */
public class RecorderView extends JFrame {

    /**
     *
     * @param mw the parent window
     */
    public RecorderView(MainWindow mw) {

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(900, 500));
        this.setBackground(Color.GRAY);

        JPanel container = new JPanel();
        container.setLayout(new GridLayout(4, 1));

        JLabel howToReplay = new JLabel(),
                q1 = new JLabel(),
                q2 = new JLabel(),
                q3 = new JLabel();
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);

        howToReplay.setText(" How to use the recorder");
        q1.setText("1.   Start the recorder and stop recording when you want to finish your replay");
        q2.setText("<html>2. &nbsp 0 on the slider is step by step, 100 on the slider is real time <br> &nbsp &nbsp &nbsp in between is the speed of steps in ms <html>");
        q3.setText("<html>3. &nbsp press start replay, it will only go step by step if value 0 is chosen, <br> &nbsp &nbsp &nbsp else it will go at the selected speed until the replay is finished <html>");
        container.add(howToReplay);
        howToReplay.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(q1);
        q1.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(q2);
        q2.setHorizontalAlignment(SwingConstants.LEFT);
        container.add(q3);
        q3.setHorizontalAlignment(SwingConstants.LEFT);

        howToReplay.setFont(labelFont);
        q1.setFont(labelFont);
        q2.setFont(labelFont);
        q3.setFont(labelFont);

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
                RecorderView.this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });

        this.add(container);
        setVisible(true);
    }
}
