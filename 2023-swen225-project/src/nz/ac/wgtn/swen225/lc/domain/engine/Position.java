package nz.ac.wgtn.swen225.lc.domain.engine;

/**
 * Represents a 2D vector position on a Cartesian plane.
 * This is from the origin of the screen (the top-left corner).
 * @param x The X coordinate.
 * @param y The Y coordinate.
 */
public record Position(int x, int y) {

    /**
     * Adds two position vectors together.
     */
    public Position add(Position v2) {
        return new Position(this.x+v2.x, this.y+v2.y);
    }

    /**
     * Subtracts two position vectors together.
     */
    public Position subtract(Position v2) {
        return new Position(this.x-v2.x,this.y-v2.y);
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", x, y);
    }

    /**
     * Calculates the Pythagorean distance between two vector positions.
     */
    public double distance(Position v2) {
        return Math.hypot(Math.abs(v2.x()-this.x()), Math.abs(v2.y()-this.y()));
    }

    /**
     * Determines whether this position is greater than or equal to a given position.
     */
    public boolean gtOrEq(Position v2) {
        return gt(v2) || equals(v2);
    }

    /**
     * Determines whether this position is lesser than or equal to a given position.
     */
    public boolean ltOrEq(Position v2) {
        return lt(v2) || equals(v2);
    }

    /**
     * Determines whether this position is greater than a given position.
     */
    public boolean gt(Position v2) {
        return this.x > v2.x && this.y > v2.y;
    }

    /**
     * Determines whether this position is less than a given position.
     */
    public boolean lt(Position v2) {
        return this.x < v2.x && this.y < v2.y;
    }
}
