import java.awt.geom.Point2D;

/**
 * Represents a partial stroke, a small part of a larger line (which is recorded in PencilAction)
 */
public class PartialStroke {
    /**
     * The stroke origin.
     */
    public Point2D.Double origin;

    /**
     * The stroke destination.
     */
    public Point2D.Double destination;

    public PartialStroke(Point2D.Double origin, Point2D.Double destination) {
        this.origin = origin;
        this.destination = destination;
    }
}