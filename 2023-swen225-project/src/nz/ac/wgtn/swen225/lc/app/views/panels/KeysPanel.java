package nz.ac.wgtn.swen225.lc.app.views.panels;

import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.renderer.ImagePanel;
import nz.ac.wgtn.swen225.lc.renderer.MainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * A panel with a title and keys in the players inventory displayed beneath
 *
 * @author sharmarene - 300610529
 */
public class KeysPanel extends ApplicationPanel {

    /**
     * Initialise a KeysPanel
     *
     * @param title what will be passed to the superclass and the text of the title
     * @param model the current state of the game to interpret the amount of keys
     */
    public KeysPanel(String title, GameModel model) {
        super(title);

        BufferedImage keyTileImage = null;

        JPanel imageContainer = new JPanel();
        imageContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        try {
            keyTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/app/keyTile.png")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (Color color : model.keys()) {
            ImagePanel imagePanel = new ImagePanel(keyTileImage, color);
            imagePanel.setPreferredSize(50, 50);
            imageContainer.add(imagePanel);
        }

        imageContainer.setBackground(super.getPanelBackgroundColor());
        imageContainer.setVisible(true);


        this.add(imageContainer, BorderLayout.CENTER);
    }
}
