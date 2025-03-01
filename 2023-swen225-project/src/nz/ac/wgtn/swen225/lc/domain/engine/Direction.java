package nz.ac.wgtn.swen225.lc.domain.engine;

/**
 * Represents a translatable direction in a 2D Cartesian plane.
 */
public enum Direction {
    /**
     * Left (x -= 1)
     */
    LEFT,
    /**
     * Up (y -= 1)
     */
    UP,
    /**
     * Down (y += 1)
     */
    DOWN,
    /**
     * Right (x += 1)
     */
    RIGHT;

    /**
     * Returns a 2D Position vector containing the unit translation of this direction.
     */
    public Position getTranslation() {
        return switch (this) {
            case LEFT -> new Position(-1, 0);
            case UP -> new Position(0,-1);
            case DOWN -> new Position(0, 1);
            case RIGHT -> new Position(1, 0);
        };
    }
}
