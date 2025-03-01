package nz.ac.wgtn.swen225.lc.app.views.panels;

import javax.swing.*;
import java.awt.*;

/**
 * A simple panel with a title and a label
 *
 * @author sharmarene - 300610529
 */
public class OtherPanel extends ApplicationPanel {

    private final JLabel info = new JLabel();

    /**
     * Initialise an Other Panel
     *
     * @param title what will be passed to the superclass and the text of the title
     * @param value the value that will be displayed beneath the title
     */
    public OtherPanel(String title, String value) {
        super(title);

        this.add(info, BorderLayout.CENTER);
        info.setFont(super.getInfoFont());
        info.setText(value);
        info.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void changeFontSize(int size) {
        info.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
    }
}
