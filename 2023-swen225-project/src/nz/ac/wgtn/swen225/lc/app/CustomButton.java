package nz.ac.wgtn.swen225.lc.app;

import javax.swing.*;
import java.awt.*;

/**
 * A custom button that has a preset size, font size and is auto de-focused
 *
 * @author renesharma - 300610529
 */
public class CustomButton extends JButton {

    public CustomButton(String text) {
        this.setText(text);
        this.setFocusable(false);
        this.setPreferredSize(new Dimension(140, 30));
        this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    }
}
