import javafx.util.Pair;

import java.util.*;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. For each edge in the graph add the corresponding cities to the fromLinks and toLinks
    public static void computeLinks(Graph graph){
        for (Edge e : graph.getOriginalEdges()) {
            e.toCity().addFromLinks(e.fromCity());
            e.fromCity().addToLinks(e.toCity());
        }
        printPageRankGraphData(graph);
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        Map<City, PageRankResult> pagerank = calculatePageRankExpanded(graph);

        for (var entry : pagerank.entrySet().stream().sorted(Comparator.comparing(a -> a.getKey().getName())).toList()) {
            City helpful = calculateMostHelpfulNeighbour(entry.getValue());
            System.out.println(entry.getKey().getName() + "[" + entry.getKey().getId() + "]: " +
                    entry.getValue().ranking);

        }

        System.out.println();
        System.out.println("Challenge:");
        for (var entry : pagerank.entrySet().stream().sorted(Comparator.comparing(a -> a.getKey().getName())).toList()) {
            City helpful = calculateMostHelpfulNeighbour(entry.getValue());
            System.out.println("Node " + entry.getKey().getName() + ": " + (helpful == null ? "None" : helpful.getName()));

        }
    }

    static class PageRankResult {
        public double ranking;
        public Map<City, Double> contributions = new HashMap<>();
        public PageRankResult(double ranking) { this.ranking = ranking;}
    }

    public static Map<City, PageRankResult> calculatePageRankExpanded(Graph graph) {
        var numberOfNodes = graph.getCities().values().size();
        Map<City, PageRankResult> pagerank = new HashMap<>();
        for (City c : graph.getCities().values()) {
            pagerank.put(c, new PageRankResult(1.0/numberOfNodes));
        }
        for (int i = 0; i < iter; i++) {
            var npr = new HashMap<City, PageRankResult>();
            for (City c : graph.getCities().values()) {
                double nRank = 0;
                var contributions = new HashMap<>(pagerank.get(c).contributions);
                for (var backNeighbour : c.getFromLinks()) {
                    var neighbourShare = pagerank.get(backNeighbour).ranking/backNeighbour.getToLinks().size();
                    if (pagerank.get(c).contributions.containsKey(backNeighbour)) {
                        contributions.put(backNeighbour, pagerank.get(c).contributions.get(backNeighbour) + neighbourShare);
                    } else {
                        contributions.put(backNeighbour, neighbourShare);
                    }
                    nRank += neighbourShare;
                }
                nRank = ((1-dampingFactor)/numberOfNodes) + (dampingFactor*nRank);
                var newprp = new PageRankResult(nRank);
                newprp.contributions = contributions;
                npr.put(c, newprp);
            }
            pagerank = npr;
        }
        return pagerank;
    }

    /**
     * Identifies the most helpful neighbour for the target city
     * (the neighbour that would drop target's ranking the most
     * if it stopped linking to target)
     */
    public static City calculateMostHelpfulNeighbour(PageRankResult result) {
        Map.Entry<City, Double> max = null;
        for (var contributor : result.contributions.entrySet()) {
            if (max == null || contributor.getValue() > max.getValue()) {
                max = contributor;
            }
        }
        return max == null ? null : max.getKey();
    }
}
