package nz.ac.wgtn.swen225.lc.app.views.panels;

import javax.swing.*;
import java.awt.*;

/**
 * A panel that is created at a constant panel size with constant colours and font sizes
 *
 * @author renesharma - 300610529
 */
abstract class ApplicationPanel extends JPanel {

    private final Font infoFont = new Font(Font.SANS_SERIF, Font.BOLD, 50) ;
    private final Color panelBackgroundColor = new Color(108, 148, 208);

    /**
     *
     * @param title the static field of the JPanel
     */
    public ApplicationPanel(String title) {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(new Color(144, 173, 194), 5));
        this.setBackground(panelBackgroundColor);
        Dimension panelSize = new Dimension(290, 100);
        this.setPreferredSize(panelSize);
        this.setMinimumSize(panelSize);

        JLabel staticLabel = new JLabel();
        staticLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        staticLabel.setText(title);
        staticLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(staticLabel, BorderLayout.NORTH);

    }

    /**
     *
     * @return the font
     */
    public Font getInfoFont() {
        return infoFont;
    }

    /**
     *
     * @return the color of the background of the panel
     */
    public Color getPanelBackgroundColor() {
        return panelBackgroundColor;
    }
}
