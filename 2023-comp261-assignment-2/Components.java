import java.util.*;
import java.util.stream.Collectors;

//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in and
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

public class Components{

    // Use Kosaraju's algorithm.
    // In the forward search, record which nodes are visited with a visited set.
    // In the backward search, use the setSubGraphId and getSubGraphID methods
    // on the stop to record the component (and whether the node has been visited
    // during the backward search).
    // Alternatively, during the backward pass, you could use a Map<Stop,Stop>
    // to record the "root" node of each component, following the original version
    // of Kosaraju's algorithm, but this is unnecessarily complex.
    public static void findComponents(Graph graph) {
        System.out.println("calling findComponents");
        graph.setSubGraphCount(0);

        var componentNumber = 0;
        var nodeList = new ArrayList<Stop>();
        var visited = new HashSet<Stop>();

        for (var node : graph.getStops()) {
            forwardVisit(node, nodeList, visited);
        }

        Collections.reverse(nodeList);
        for (var node : nodeList) {
            if (node.getSubGraphId() == -1) {
                backwardVisit(node, componentNumber);
                componentNumber++;
            }
        }
        graph.setSubGraphCount(componentNumber);
    }

    public static void forwardVisit(Stop stop, ArrayList<Stop> stopList, HashSet<Stop> visited) {
        if (!visited.contains(stop) && stop.getSubGraphId() == -1) {
            visited.add(stop);
            for (Edge edge : stop.getForwardEdges()) {
                forwardVisit(edge.toStop(), stopList, visited);
            }
            stopList.add(stop);
        }
    }

    public static void backwardVisit(Stop stop, int componentNumber) {
        if (stop.getSubGraphId() == -1) {
            stop.setSubGraphId(componentNumber);
            for (Edge edge : stop.getBackwardEdges()) {
                backwardVisit(edge.fromStop(), componentNumber);
            }
        }
    }
}
