package nz.ac.wgtn.swen225.lc.renderer;

import nz.ac.wgtn.swen225.lc.domain.engine.Direction;
import nz.ac.wgtn.swen225.lc.domain.state.DirectionChangeEvent;
import nz.ac.wgtn.swen225.lc.domain.state.GameModel;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The SpriteEntity class represents an animated sprite entity that can change its appearance based on the current direction.
 * can either be reactive to the game Model or just static
 *
 * @author burkitkahu - 300604675
 */
public class SpriteEntity extends ImagePanel implements PropertyChangeListener {
    BufferedImage currentFrameImage;
    private final int spriteSheetRows;
    private final int spriteSheetCol;
    private final int frameSize;
    public Direction currentDirection = Direction.DOWN;

    /**
     * Constructs a SpriteEntity with the specified sprite sheet and animation parameters.
     *
     * @param spriteSheet     The sprite sheet containing different frames for animation.
     * @param spriteSheetRows The number of rows in the sprite sheet.
     * @param spriteSheetCol  The number of columns in the sprite sheet.
     * @param frameSize       The size of each frame.
     */
    public SpriteEntity(BufferedImage spriteSheet, int spriteSheetRows, int spriteSheetCol, int frameSize) {
        super(spriteSheet);
        this.currentFrameImage = spriteSheet.getSubimage(0, 0, frameSize, frameSize);
        this.spriteSheetRows = spriteSheetRows;
        this.spriteSheetCol = spriteSheetCol;
        this.frameSize = frameSize;
    }

    /**
     * Constructs a SpriteEntity with the specified sprite sheet, animation parameters, and a GameModel.
     * This constructor also registers itself as a property change listener to the GameModel for handling position changes.
     *
     * @param spriteSheet     The sprite sheet containing different frames for animation.
     * @param spriteSheetRows The number of rows in the sprite sheet.
     * @param spriteSheetCol  The number of columns in the sprite sheet.
     * @param frameSize       The size of each frame.
     * @param gameModel       The GameModel instance that provides the game state.
     */
    public SpriteEntity(BufferedImage spriteSheet, int spriteSheetRows, int spriteSheetCol, int frameSize, GameModel gameModel) {
        super(spriteSheet);
        gameModel.addPropertyChangeListener(this);
        this.currentFrameImage = spriteSheet.getSubimage(0, 0, frameSize, frameSize);
        this.spriteSheetRows = spriteSheetRows;
        this.spriteSheetCol = spriteSheetCol;
        this.frameSize = frameSize;
    }

    /**
     * Animates the sprite based on the current direction and frame counter.
     *
     * @return An ImagePanel displaying the animated sprite frame.
     */
    public ImagePanel animateSprite() {
        int currentOffset = BoardView.frameCounter % spriteSheetCol;
        switch (currentDirection) {
            case UP -> {
                return new ImagePanel(super.image.getSubimage(currentOffset * frameSize, frameSize * 2, frameSize - 1, frameSize));
            }
            case DOWN -> {
                return new ImagePanel(super.image.getSubimage(currentOffset * frameSize, frameSize * 3, frameSize - 1, frameSize));
            }
            case LEFT -> {
                return new ImagePanel(currentFrameImage = super.image.getSubimage(currentOffset * frameSize, frameSize, frameSize - 1, frameSize));
            }
            case RIGHT -> {
                return new ImagePanel(currentFrameImage = super.image.getSubimage(currentOffset * frameSize, 0, frameSize - 1, frameSize));
            }
        }
        throw new RuntimeException("Invalid direction.");
    }

    /**
     * {@inheritDoc}
     *
     * Handles property change events, specifically for the "position" property.
     * <p>
     * If the property name is "position," this method retrieves the new direction from the event
     * using a cast to the DirectionChangeEvent type. The cast is justified because it is
     * expected that only DirectionChangeEvent objects will have "position" as their property name.
     * The retrieved direction is then assigned to the currentDirection field.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt instanceof DirectionChangeEvent pd) {
            this.currentDirection = pd.direction();
        }
    }
}
