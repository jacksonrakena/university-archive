
import java.sql.Array;
import java.util.*;

import javafx.util.Pair;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<>();

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    
    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        edges = new HashMap<>();
        int i = 0;
        for (Edge e : graph.getOriginalEdges()) {
            edges.put(Integer.toString(i), new Edge(
                     e.fromCity(), e.toCity(), e.transpType(),
                    e.capacity() - e.flow(), e.flow()
            ));
            e.fromCity().addEdgeId(Integer.toString(i));
            i++;

            edges.put(Integer.toString(i), new Edge(
                    e.toCity(), e.fromCity(), e.transpType(), e.flow(), 0
                    //e.toCity(), e.fromCity(), e.transpType(), 0, e.capacity()
            ));
            e.toCity().addEdgeId(Integer.toString(i));
            i++;
        }
        printResidualGraphData(graph);
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }


    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        computeResidualGraph(graph);
        for (Edge e : edges.values()) {
            e.setFlow(0);
        }
        var maxFlow = 0;

        augmentationPaths = new ArrayList<>();


        var path = bfs(graph, from, to);

        while (path != null) {
            System.out.println("Found path: " + path.getKey() + " with bottleneck " + path.getValue());
            var pathFlow = path.getValue();
            maxFlow += pathFlow;
            augmentationPaths.add(path);

            for (String e : path.getKey()) {
                var edgeId = Integer.parseInt(e);
                edges.get(e).setFlow(edges.get(e).flow()+pathFlow);
                edges.get(e).setCapacity(edges.get(e).capacity()-pathFlow);

                var reverse = edges.get(Integer.toString(edgeId+1));
                reverse.setCapacity(reverse.capacity()+pathFlow);
            }
            path = bfs(graph,from,to);
        }
        System.out.println("Max flow: " + maxFlow);
        return augmentationPaths;
    }

    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {
        ArrayList<String> augmentationPath = new ArrayList<String>();
        HashMap<String, String> backPointer = new HashMap<String, String>();
        var queue = new ArrayDeque<City>();
        queue.push(s);
        for (City c : graph.getCities().values()) {
            backPointer.put(c.getId(), null);
        }

        while (!queue.isEmpty()) {
            var current = queue.poll();
            for (var edgePair : edges.entrySet()) {
                var edge = edgePair.getValue();
                if (edge.fromCity().getId().equals(current.getId())
                        && !edge.toCity().getId().equals(s.getId())
                        && backPointer.get(edge.toCity().getId()) == null
                        && edge.capacity() != 0) {
                    backPointer.put(edge.toCity().getId(), edgePair.getKey());

                    if (backPointer.get(t.getId()) != null) {
                        var pathEdge = backPointer.get(t.getId());
                        var bottleneck = Integer.MAX_VALUE;
                        while (pathEdge != null) {
                            augmentationPath.add(pathEdge);
                            if (getEdge(pathEdge).capacity() < bottleneck) {
                                bottleneck = getEdge(pathEdge).capacity();
                            }
                            pathEdge = backPointer.get(getEdge(pathEdge).fromCity().getId());
                        }
                        Collections.reverse(augmentationPath);
                        return new Pair<>(augmentationPath, bottleneck);
                    }
                    queue.push(edge.toCity());
                }
            }
        }
        return null;
    }
}


