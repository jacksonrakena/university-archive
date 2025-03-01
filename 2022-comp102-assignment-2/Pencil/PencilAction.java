import java.util.LinkedList;

public class PencilAction {
    public LinkedList<PartialStroke> strokes = new LinkedList<>();

    public PencilState state;

    public void addStroke(PartialStroke stroke) {
        this.strokes.add(stroke);
    }

    public PencilAction(PencilState state) {
        this.state = state;
    }
}
