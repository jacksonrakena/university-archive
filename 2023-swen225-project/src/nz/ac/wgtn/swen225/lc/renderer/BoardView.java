package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;
import nz.ac.wgtn.swen225.lc.domain.world.tiles.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Objects;

/**
 * The BoardView class controls the visual representation of the game board on the screen.
 * It is split into three layers: tiles, enemies, and the player using a JLayeredPane.
 *
 * @author burkitkahu - 300604675
 */
public class BoardView extends JLayeredPane implements PropertyChangeListener {

    public static int frameCounter;
    public static Timer frameTimer;

    // Animated entities
    private final SpriteEntity playerEntity;
    private final SpriteEntity enemyEntity;
    private final JPanel infoLayer;
    private final JPanel entityAnimationLayer;
    private final JPanel boardLayer;
    public final SoundControl soundControl;

    private final GameModel model;

    // Constants
    public final Dimension BOARD_SIZE = new Dimension(720, 720);
    public final static int TILE_SIZE = 80;

    /*
     * Assets
     */
    private BufferedImage freeTileImage;
    private BufferedImage wallTileImage;
    private BufferedImage keyTileImage;
    private BufferedImage lockedDoorImage;
    private BufferedImage infoTileImage;
    private BufferedImage exitLockTileImage;
    private BufferedImage exitTileImage;
    private BufferedImage treasureTileImage;
    private BufferedImage playerSpriteSheet;
    private BufferedImage enemySpriteSheet;
    private BufferedImage infoPanelBackground;
    private final Graphics boardGfx;

    /**
     * Creates a BoardView instance.
     *
     * @param gameModel The model responsible for providing the view with required information.
     */
    public BoardView(GameModel gameModel) {
        soundControl = new SoundControl();
        soundControl.playSong();

        BufferedImage boardImage = new BufferedImage(TILE_SIZE * 9, TILE_SIZE * 9, BufferedImage.TYPE_INT_RGB);
        boardGfx = boardImage.getGraphics();
        boardLayer = new ImagePanel(boardImage);
        gameModel.addPropertyChangeListener(this);
        boardLayer.setBounds(0, 0, 720, 720);

        model = gameModel;
        this.setSize(BOARD_SIZE);
        loadAssets();

        entityAnimationLayer = new JPanel();
        entityAnimationLayer.setBounds(0, 0, BOARD_SIZE.width, BOARD_SIZE.height);
        entityAnimationLayer.setOpaque(false);
        this.add(entityAnimationLayer, Integer.valueOf(2));

        playerEntity = new SpriteEntity(playerSpriteSheet, 4, 3, TILE_SIZE, gameModel);
        enemyEntity = new SpriteEntity(enemySpriteSheet, 4, 3, TILE_SIZE);

        infoLayer = new ImagePanel(infoPanelBackground);
        infoLayer.setLayout(new GridBagLayout());
        infoLayer.setBounds(TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 7, TILE_SIZE * 3);
        infoLayer.setOpaque(true);
        infoLayer.setVisible(false);
        this.add(infoLayer, Integer.valueOf(3));

        this.add(boardLayer, Integer.valueOf(0));
        this.setVisible(true);

        frameTimer = new Timer(100, e -> {
            animateSprites();
            frameCounter++;
        });
        frameTimer.setRepeats(true);
        frameTimer.start();
        updateBoard();
    }

    /**
     * Loads all the visual assets for the board.
     */
    private void loadAssets() {
        try {
            freeTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/freeTile.png")));
            wallTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/wall.png")));
            infoTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/infoTile.png")));
            lockedDoorImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/lockedDoor.png")));
            keyTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/keyTile.png")));
            exitTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/exitTile.png")));
            treasureTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/treasureTile.png")));
            exitLockTileImage = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/tiles/exitLockTile.png")));
            playerSpriteSheet = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/amogusPlayerSpriteSheet.png")));
            enemySpriteSheet = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/BadActorSpriteSheet.png")));
            infoPanelBackground = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/renderer/Among-Us-Voting-Session.png")));
        } catch (IOException e) {
            System.out.println("FAILED TO LOAD ASSETS");
        }
    }

    /**
     * Updates the position of the view to center on the player.
     */
    private void updateBoard() {
        int modelViewOffsetX = this.model.position().x() - 4;
        int modelViewOffsetY = this.model.position().y() - 4;
        int row = 0;
        for (int mvy = modelViewOffsetY; mvy <= this.model.position().y() + 4; mvy++) {
            int col = 0;
            for (int mvx = modelViewOffsetX; mvx <= this.model.position().x() + 4; mvx++) {
                BufferedImage image;
                var color = Color.WHITE;
                if (mvx >= this.model.tiles().length || mvy >= this.model.tiles().length || mvx < 0 || mvy < 0) {
                    image = freeTileImage;
                } else {
                    var currentTile = this.model.tiles()[mvy][mvx];
                    if (currentTile instanceof ExitLockTile) {
                        image = exitLockTileImage;
                    } else if (currentTile instanceof ExitTile) {
                        image = exitTileImage;
                    } else if (currentTile instanceof FreeTile) {
                        image = freeTileImage;
                    } else if (currentTile instanceof InfoTile) {
                        image = infoTileImage;
                    } else if (currentTile instanceof KeyTile) {
                        color = ((KeyTile) currentTile).getColour();
                        image = keyTileImage;
                    } else if (currentTile instanceof LockedDoorTile) {
                        color = ((LockedDoorTile) currentTile).getColour();
                        image = lockedDoorImage;
                    } else if (currentTile instanceof TreasureTile) {
                        image = treasureTileImage;
                    } else if (currentTile instanceof WallTile) {
                        image = wallTileImage;
                    } else {
                        throw new IllegalArgumentException("Tile type does not exist / cannot be rendered");
                    }
                }
                boardGfx.drawImage(image, col * TILE_SIZE, row * TILE_SIZE, color, null);
                col++;
            }
            row++;
        }
        this.boardLayer.repaint();
    }

    /**
     * Draws the help message on the info layer.
     */
    public void drawHelp() {
        infoLayer.removeAll();
        String message = model.helpMessage();
        JLabel messagePanel = new JLabel(message);
        messagePanel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        infoLayer.add(messagePanel);
        infoLayer.setVisible(true);
    }

    /**
     * Animates the player and enemies.
     */
    private void animateSprites() {
        ImagePanel playerFrame = playerEntity.animateSprite();
        playerFrame.setBounds(TILE_SIZE * 4, TILE_SIZE * 4, TILE_SIZE, TILE_SIZE);
        playerFrame.setOpaque(false);
        entityAnimationLayer.removeAll();
        entityAnimationLayer.add(playerFrame);

        for (var agent : this.model.agents()) {
            var playerViewBoundMin = this.model.position().subtract(new Position(5, 5));
            var playerViewBoundMax = this.model.position().add(new Position(5, 5));

            if (agent.position.ltOrEq(playerViewBoundMax) && agent.position.gtOrEq(playerViewBoundMin)) {
                var newAgentPosition = agent.position.subtract(this.model.position().subtract(new Position(4, 4)));
                enemyEntity.currentDirection = agent.direction;
                var agentFrame = enemyEntity.animateSprite();
                agentFrame.setBounds(TILE_SIZE * newAgentPosition.x(), TILE_SIZE * newAgentPosition.y(), TILE_SIZE, TILE_SIZE);
                agentFrame.setOpaque(false);
                entityAnimationLayer.add(agentFrame);
            }
        }

        entityAnimationLayer.repaint();
    }

    /**
     * Handles property change events.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "tiles" -> this.updateBoard();
            case "remainingTreasures", "keys" -> soundControl.playSound(SoundControl.SoundList.pickup);
            case "position" -> {
                this.infoLayer.setVisible(false);
                soundControl.playSound(SoundControl.SoundList.walking);
                this.updateBoard();
            }
            case "helpMessage" -> drawHelp();
            default -> {
                return;
            }
        }
    }
}
