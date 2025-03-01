package nz.ac.wgtn.swen225.lc.renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A custom JPanel that displays an image. If the image is transparent, a background color can be provided to fill the
 * area behind the image.
 *
 * @author burkitkahu - 300604675
 */
public class ImagePanel extends JPanel {
    protected BufferedImage image;

    /**
     * Constructs an ImagePanel with the specified image.
     *
     * @param b The image to be displayed on the panel.
     */
    public ImagePanel(BufferedImage b) {
        this.image = b;
    }

    /**
     * Constructs an ImagePanel with the specified image and background color.
     *
     * @param b               The image to be displayed on the panel.
     * @param backgroundColor The background color to fill behind the image if it's transparent.
     */
    public ImagePanel(BufferedImage b, Color backgroundColor) {
        this(b);
        this.setBackground(backgroundColor);
    }

    /**
     * Overrides the paintComponent method to draw the image on the panel.
     *
     * @param g The Graphics object used for drawing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

    /**
     * Sets the preferred size of the panel with the specified width and height.
     *
     * @param width  The preferred width of the panel.
     * @param height The preferred height of the panel.
     */
    public void setPreferredSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }
}
