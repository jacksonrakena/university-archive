// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 1
 * Name:
 * Username:
 * ID:
 */

/** 
 * Object containing the record of an action (move or push) in
 * a given direction.
 * Used for the Undo process.
 * Every move or push should put an ActionRecord on the history stack
 * Undo should pop an ActionRecord off the history stack and
 *  undo the recorded action.
 */

public class ActionRecord {
    private final String recordAction;
    private final String direction; // direction of the move or push
    private final Position teleportPreviousPosition;
    private final Position teleportNextPosition;

    /**
     * Constructor
     */
    public ActionRecord(String action, String dir) {
        recordAction = action;
        direction = dir;
        this.teleportPreviousPosition = null;
        this.teleportNextPosition = null;
    }

    public Position getPreviousPositionForTeleport() {
        return this.teleportPreviousPosition;
    }

    public Position getNextPositionForTeleport() {
        return this.teleportNextPosition;
    }

    public ActionRecord(String action, String dir, Position teleportPreviousPosition, Position teleportNextPosition) {
        recordAction = action;
        direction = dir;
        this.teleportPreviousPosition = new Position(teleportPreviousPosition.row, teleportPreviousPosition.col);
        this.teleportNextPosition = new Position(teleportNextPosition.row, teleportNextPosition.col);
    }

    /**
     * Is the recorded action a push?
     */
    public boolean isPush() {
        return this.recordAction.equalsIgnoreCase("push");
    }

    public String getAction() {
        return this.recordAction;
    }

    /**
     * Is the recorded action a move?
     */
    public boolean isMove() {
        return this.recordAction.equalsIgnoreCase("move");
    }

    /**
     * Return the direction of the recorded action
     */
    public String direction() {
        return direction;
    }

    /**
     * Return a String describing the recorded action
     */
    public String toString() {
        return ((isPush() ? "Push" : "Move") + " to " + direction);
    }

}
