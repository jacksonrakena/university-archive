/**
 * Implements the A* search algorithm to find the shortest path
 * in a graph between a start node and a goal node.
 * It returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.*;

import java.util.stream.Collectors;


public class AStar {
    private static String timeOrDistance = "distance";    // way of calculating cost: "time" or "distance"

    // find the shortest path between two stops
    public static List<Edge> findShortestPath(Stop start, Stop goal, String tod) {
        if (start == null || goal == null) {return null;}
        timeOrDistance= (tod.equals("time"))?"time":"distance";

        var pq = new PriorityQueue<>(PathItem::compareTo);
        var backptrs = new HashMap<Stop, Edge>();

        pq.add(new PathItem(start, null, 0, heuristic(start,goal)));
        var visitedStops = new HashSet<Stop>();

        while (!pq.isEmpty()) {
            var fringe = pq.poll();
            if (!visitedStops.contains(fringe.currentStop)) {
                visitedStops.add(fringe.currentStop);
                backptrs.put(fringe.currentStop, fringe.edgeTakenToGetToStop);
                if (fringe.currentStop == goal) {
                    var out = reconstruct(start, goal, backptrs);
                    for (var d : out) {
                        System.out.println(d);
                    }
                    return out;
                }
                for (Edge e : fringe.currentStop.getForwardEdges()) {
                    if (!backptrs.containsKey(e.toStop())) {
                        Stop neighbour = e.toStop();
                        double lengthToNeighbour = fringe.totalLengthSoFar + edgeCost(e);
                        double estimateTotalPath = lengthToNeighbour + heuristic(neighbour, goal);
                        pq.add(new PathItem(e.toStop(), e, lengthToNeighbour, estimateTotalPath));
                    }
                }
            }
        }

        return null;
    }

    public static List<Edge> reconstruct(Stop start, Stop goal, HashMap<Stop, Edge> backpointers) {
        if (start == goal) return new ArrayList<>();
        var output = new ArrayList<Edge>();
        Edge e = backpointers.get(goal);
        while (true) {
            Edge to = e;
            Edge from = backpointers.get(to.fromStop());
            output.add(to);
            if (to.fromStop() == start) {
                Collections.reverse(output);
                return output;
            }
            e = from;
        }
    }

    /** Return the heuristic estimate of the cost to get from a stop to the goal */
    public static double heuristic(Stop current, Stop goal) {
        if (timeOrDistance.equals("distance")) { return current.distanceTo(goal);}
        else if (timeOrDistance.equals("time")){return current.distanceTo(goal) / Transport.TRAIN_SPEED_MPS;}
        else {return 0;}
    }

    /** Return the cost of traversing an edge in the graph */
    public static double edgeCost(Edge edge){
        if (timeOrDistance.equals("distance")){ return edge.distance();}
        else if (timeOrDistance.equals("time")){return edge.time();}
        else {return 1;}
    }




}
