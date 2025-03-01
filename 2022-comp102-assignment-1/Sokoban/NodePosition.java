public class NodePosition extends Position{
    /**
     * Constructor
     *
     * @param row
     * @param col
     */
    int value;
    Position inner;
    NodePosition(Position pos, int value) {
        super(pos.row, pos.col);
        this.value = value;
        inner = pos;
    }
}
