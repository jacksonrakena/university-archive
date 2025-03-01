package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.app.LarryCroftsAdventures;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * The MainMenu class represents the main menu of the game.
 * @author burkitkahu - 300604675
 */
public class MainMenu extends JFrame {
    private int frameCount = 0;

    // Main menu assets
    BufferedImage title;
    BufferedImage mainMenu;
    BufferedImage spritePlayerImage;
    BufferedImage enemyPlayerImage;
    JLayeredPane container;
    JPanel playerContainer;
    JPanel topContainer;

    ImagePanel[] animatePlayerFrames = new ImagePanel[3];
    ImagePanel[] animateEnemyFrames = new ImagePanel[3];
    public final Dimension MAIN_MENU_SIZE = new Dimension(400, 600);

    /**
     * Constructs the main menu and initializes its components.
     */
    public MainMenu() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);
        this.setSize((int) MAIN_MENU_SIZE.getWidth(), (int) MAIN_MENU_SIZE.getHeight());

        loadAssets();
        loadAnimationFrames();

        container = new JLayeredPane();
        container.setBounds(0, 0, MAIN_MENU_SIZE.width, MAIN_MENU_SIZE.height);

        // Background
        ImagePanel background = new ImagePanel(mainMenu);
        background.setBounds(0, 0, MAIN_MENU_SIZE.width, MAIN_MENU_SIZE.height);
        container.add(background, Integer.valueOf(0));

        // Button
        topContainer = new JPanel();
        topContainer.setLayout(null);
        ImagePanel titleImagePane = new ImagePanel(title);
        titleImagePane.setOpaque(false);
        titleImagePane.setBounds(47, 80, 350, 156);
        topContainer.add(titleImagePane);

        JButton playButton = styledButtonMaker(new JButton("Play"));
        playButton.addActionListener(onClick -> {
            play();
        });
        playButton.setBounds(100, 280, 180, 60);

        topContainer.add(playButton);

        topContainer.setBounds(0, 0, MAIN_MENU_SIZE.width, MAIN_MENU_SIZE.height / 2 + 100);
        topContainer.setOpaque(false);
        container.add(topContainer, Integer.valueOf(1));

        playerContainer = new JPanel();
        playerContainer.setBounds(0, (MAIN_MENU_SIZE.height / 2) + 60, MAIN_MENU_SIZE.width, MAIN_MENU_SIZE.height / 2);
        playerContainer.setOpaque(false);
        container.add(playerContainer, Integer.valueOf(2));

        this.getContentPane().add(container);

        Timer animate = new Timer(100, e -> animateSprite());
        animate.start();
        this.setVisible(true);
    }

    private JButton styledButtonMaker(JButton b) {
        b.setBackground(Color.white);
        b.setBorder(new EmptyBorder(40, 0, 40, 0));
        return b;
    }

    private void loadAnimationFrames() {
        for (int c = 0; c < 3; c++) {
            animatePlayerFrames[c] = new ImagePanel(spritePlayerImage.getSubimage(c * 80, 0, 80 - 1, 80));
            animateEnemyFrames[c] = new ImagePanel(enemyPlayerImage.getSubimage(c * 80, 0, 80 - 1, 80));
        }
    }

    private void loadAssets() {
        try {
            spritePlayerImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/amogusPlayerSpriteSheet.png")));
            enemyPlayerImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/BadActorSpriteSheet.png")));
            mainMenu = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/mainMenuBackground.png")));
            title = ImageIO.read(Objects.requireNonNull(MainMenu.class.getResource("/renderer/Title.png")));
        } catch (IOException e) {
            System.out.println("Failed to load");
        }
    }

    private void play() {
        new LarryCroftsAdventures();
        this.setVisible(false);
    }

    private void animateSprite() {
        frameCount++;
        if (frameCount >= 3) frameCount = 0;
        ImagePanel playerSpriteCurrentFrame = animatePlayerFrames[frameCount];
        playerSpriteCurrentFrame.setBounds(210, 40, 80, 80);
        playerSpriteCurrentFrame.setOpaque(false);

        ImagePanel enemySpriteCurrentFrame = animateEnemyFrames[frameCount];
        enemySpriteCurrentFrame.setBounds(110, 40, 80, 80);
        enemySpriteCurrentFrame.setOpaque(false);

        playerContainer.removeAll();

        playerContainer.add(playerSpriteCurrentFrame);
        playerContainer.add(enemySpriteCurrentFrame);

        playerContainer.repaint();
    }
}
