import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

//=============================================================================
//   TODO   Finding Articulation Points
//   Finds and returns a collection of all the articulation points in the undirected
//   graph, without walking edges
//=============================================================================

public class ArticulationPoints{

    // Use the algorithm from the lectures, but you will need a loop to check through
    // all the Stops in the graph to find any Stops which were not connected to the
    // previous Stops, and apply the lecture slide algorithm starting at each such stop.

    public static Map<Stop,Integer> depthMap = new HashMap<>();
    public static Set<Stop> articulatedStops = new HashSet<>();

    // Returns the collection of nodes that are articulation points 
    // in the UNDIRECTED graph with no walking edges.


    public static Collection<Stop> findArticulationPoints(Graph graph) {
        System.out.println("calling findArticulationPoints");
        graph.computeNeighbours();   // To ensure that all stops have a set of (undirected) neighbour stops

        Set<Stop> articulationPoints = new HashSet<Stop>();
        articulatedStops = new HashSet<>();
        depthMap = new HashMap<>();

        for (var node : graph.getStops()) {
            depthMap.put(node, -1);
        }

        var numberOfSubtrees = 0;
        var start = graph.getStops().stream().findFirst().get();
        depthMap.put(start,0);
        for (var neighbour : start.getNeighbours()) {
            if (getOrSetDefault(neighbour) == -1) {
                articulateDepthFirst(neighbour, 1, start, articulationPoints);
                numberOfSubtrees++;
            }
        }
        if (numberOfSubtrees > 1) {
            articulationPoints.add(start);
        }


        for (var node : graph.getStops()) {
            if (!articulatedStops.contains(node)) {
                for (var neighbour : node.getNeighbours()) {
                    if (!articulatedStops.contains(neighbour)) {
                        articulateDepthFirst(neighbour, 1, node, articulationPoints);
                        numberOfSubtrees++;
                    }
                }
            }
        }
        System.out.println("Found " + articulationPoints.size() + " total articulation points.");
        return articulationPoints;
    }


    public static int articulateDepthFirst(Stop node, int depth, Stop fromNode, Set<Stop> articulationPoints) {
        articulatedStops.add(node);
        depthMap.put(node, depth);
        var reachBack = depth;
        for (var neighbour : node.getNeighbours()) {
            if (neighbour != fromNode) {
                if (getOrSetDefault(neighbour) != -1) {
                    reachBack = Math.min(getOrSetDefault(neighbour), reachBack);
                } else {
                    var childReach = articulateDepthFirst(neighbour, depth+1, node, articulationPoints);
                    if (childReach >= depth) {
                        articulationPoints.add(node);
                    }
                    reachBack = Math.min(childReach, reachBack);
                }
            }
        }
        return reachBack;
    }

    public static int getOrSetDefault(Stop stop) {
        if (depthMap.containsKey(stop)) return depthMap.get(stop);
        depthMap.put(stop, -1);
        return -1;
    }
}
