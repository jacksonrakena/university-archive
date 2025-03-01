/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */

public class PathItem implements Comparable<PathItem> {
    public Stop currentStop;
    public Edge edgeTakenToGetToStop;
    public double totalLengthSoFar;
    public double estimateTotalPath;

    public PathItem(Stop target, Edge edge, double lengthToStop, double estimateTotalPath) {
        this.currentStop = target;
        this.edgeTakenToGetToStop = edge;
        this.totalLengthSoFar = lengthToStop;
        this.estimateTotalPath = estimateTotalPath;
    }
    @Override
    public int compareTo(PathItem o) {
        return Double.compare(this.estimateTotalPath, o.estimateTotalPath);
    }
}
