import java.awt.*;

/**
 * Represents the current 'state' of the pencil.
 * For example, it contains the stroke width and stroke color.
 * This class can be passed around and easily cloned to save a record of this object.
 */
public class PencilState {
    /**
     * The width of the pencil's strokes.
     */
    public double strokeWidth;

    /**
     * The color of the pencil.
     */
    public Color strokeColor;

    public PencilState(double strokeWidth, Color strokeColor) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
    }

    /**
     * Returns a user-friendly description of the current state of this pencil.
     */
    @Override
    public String toString() {
        return "stroke width of " + strokeWidth + "px, and color of " + strokeColor.toString();
    }

    /**
     * Clones this state, returning a new object with the same properties.
     */
    public PencilState clone() {
        try {
            return (PencilState) super.clone();
        } catch (CloneNotSupportedException e) {
            return new PencilState(strokeWidth, strokeColor);
        }
    }
}
