// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    /*# YOUR CODE HERE */

    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    public List<Station> allStations;
    public List<TrainLine> allTrainLines;
    public List<TrainService> allTrainServices;

    public Map<Integer, Double> fares;

    private static boolean loadedData = false;  // used to ensure that the program is called from main.

    private boolean geographicMap = true;
    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    public void highlightStationOnMap(Station station, String text) {
        double highlightCircleWidth = 12;
        UI.setColor(Color.yellow);
        UI.fillOval(station.getXCoord()-(highlightCircleWidth/2), station.getYCoord()-(highlightCircleWidth/2), highlightCircleWidth, highlightCircleWidth);
        UI.setColor(Color.black);
        UI.drawString(text, Math.max(0, station.getXCoord()+5), Math.max(0, station.getYCoord()+5));
    }

    /**
     * Loads the station data.
     */
    public void loadStationData() {
        allStations = new ArrayList<>();
        try {
            List<String> stationLines = Files.readAllLines(Path.of("data", "stations.data"));
            for (String stationDescription : stationLines) {
                String[] components = stationDescription.split(" ");
                if (components.length != 4) {
                    throw new RuntimeException("Station data invalid. Expected four points.");
                }
                String name = components[0];
                int farezone = Integer.parseInt(components[1]);
                double x = Double.parseDouble(components[2]);
                double y = Double.parseDouble(components[3]);
                allStations.add(new Station(name, farezone, x, y));
            }
            UI.println("Loaded " + allStations.size() + " stations.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a Station object from a station name.
     */
    private Station findStationByName(String name) {
        return allStations.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Loads the train line data.
     */
    public void loadTrainLineData() {
        allTrainLines = new ArrayList<>();
        allTrainServices = new ArrayList<>();
        try {
            List<String> trainLines = Files.readAllLines(Path.of("data", "train-lines.data"));
            for (String lineDescription : trainLines) {
                TrainLine line = new TrainLine(lineDescription);

                // Load and attach all the stations on this line
                List<String> stationsOnThisLine = Files.readAllLines(Path.of("data", line.getName() + "-stations.data"));
                for (String stationOnLine : stationsOnThisLine) {
                    Station station = findStationByName(stationOnLine);
                    station.addTrainLine(line);
                    line.addStation(station);
                }

                // Load and attach all the services and associated times on this line
                List<String> servicesOnThisLine = Files.readAllLines(Path.of("data", line.getName() + "-services.data"));
                for (String serviceLine : servicesOnThisLine) {
                    TrainService service = new TrainService(line);
                    String[] serviceTimes = serviceLine.split(" ");
                    for (String serviceTime : serviceTimes) {
                        service.addTime(Integer.parseInt(serviceTime));
                    }
                    allTrainServices.add(service);
                    line.addTrainService(service);
                }
                allTrainLines.add(line);
            }
            UI.println("Loaded " + allTrainLines.size() + " train lines.");
            UI.println("Loaded " + allTrainServices.size() + " train services.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the fare data.
     */
    public void loadFares() {
        fares = new HashMap<>();
        try {
            List<String> fareLines = Files.readAllLines(Path.of("data", "fares.data")).stream().skip(1).collect(Collectors.toList());
            for (String fare :  fareLines) {
                String[] components = fare.split(" ");
                // fareZoneCount -> $price
                fares.put(Integer.parseInt(components[0]), Double.parseDouble(components[1]));
            }
            UI.println("Loaded " + fares.size() + " fares.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clears the screen and lists every station.
     */
    public void listAllStations() {
        UI.clearText();
        // UI.println(o) for each o in allStations
        allStations.forEach(UI::println);
    }

    /**
     * Clears the screen and lists every station, sorted in alphabetical order.
     */
    public void listStationsByName() {
        UI.clearText();
        // Use the sorted stream operator, comparing the station name, and then loop and print
        allStations.stream().sorted(Comparator.comparing(Station::getName)).forEach(UI::println);
    }

    /**
     * Lists all the train lines.
     */
    public void listAllTrainLines() {
        UI.clearText();
        allTrainLines.forEach(UI::println);
    }

    /**
     * List all the lines going through a given station.
     * @param stationName The name of the station.
     */
    public void listLinesOfStation(String stationName) {
        UI.clearText();
        // For each train line where the train line stations contains a station with the same name,
        // print it
        allTrainLines.stream()
                .filter(e -> e.getStations().stream().anyMatch(s -> s.getName().equals(stationName)))
                .forEach(UI::println);
    }

    /**
     * Lists all the stations on a specified train line.
     * @param lineName The line to list stations on.
     */
    public void listStationsOnLine(String lineName) {
        UI.clearText();
        // Find the first train line where the train line has the same name as the input, or null
        TrainLine line = allTrainLines.stream().filter(l -> l.getName().equals(lineName)).findFirst().orElse(null);
        if (line == null) {
            UI.println("Unknown line.");
            return;
        }

        line.getStations().forEach(UI::println);
    }

    /**
     * Lists all train lines with a valid route from the origin to the destination.
     * @param origin The origin station.
     * @param destination The destination station.
     */
    public void checkConnected(String origin, String destination) {
        UI.clearText();
        List<TrainLine> candidates = allTrainLines
                .stream()
                /**
                 * Filter all train lines, only take train lines
                 * that contain the origin station
                 * and the destination station
                 * and the index of the destination being larger than the index of the origin
                 */
                .filter(l ->
                        {
                            List<Station> stations = l.getStations();
                            Station originStation = stations.stream().filter(e -> e.getName().equals(origin)).findFirst().orElse(null);
                            Station destinationStation = stations.stream().filter(e -> e.getName().equals(destination)).findFirst().orElse(null);
                            if (originStation == null || destinationStation == null) return false;
                            return stations.indexOf(originStation) < stations.indexOf(destinationStation);
                        }
                )
                .collect(Collectors.toList());
        if (candidates.size() == 0) {
            UI.println("Those stations aren't connected.");
            return;
        }
        UI.println("Found " + candidates.size() + " lines:");
        candidates.forEach(UI::println);
    }

    /**
     * Finds the next services on all lines after the specified time, at a given station.
     * @param stationName The name of the station to get lines at.
     * @param time The desired start time.
     */
    public void findNextServices(String stationName, int time) {
        UI.clearText();
        boolean foundAnyCandidates = false;
        Station station = findStationByName(stationName);
        if (station == null) {
            UI.println("Can't find that station.");
            return;
        }
        for (TrainLine line : station.getTrainLines()) {
            List<Station> stations = line.getStations();
            if (stations.stream().anyMatch(s -> s.getName().equals(stationName) && stations.indexOf(s) != stations.size()-1)) {
                boolean foundServiceForLine = false;
                for (TrainService candidate : line.getTrainServices()) {
                    int stationIndex = line.getStations().indexOf(findStationByName(stationName));
                    if (stationIndex == -1) return;
                    int timeAtStation = dataTimeFormatToMinutes(candidate.getTimes().get(stationIndex));
                    if (timeAtStation > time && !foundServiceForLine && timeAtStation != -1) {
                        foundServiceForLine = true;
                        foundAnyCandidates = true;
                        UI.println(line.getName() + ": " + minutesToPrettyTime(timeAtStation));
                    }
                }
            }
        }
        if (!foundAnyCandidates) UI.println("Couldn't find any services from that station.");
    }

    /**
     * Finds a possible route between two stations, after a given time.
     * The two stations must be on the same line. (Completion)
     * @param originName The origin station.
     * @param destinationName The destination station.
     * @param startTime The time to start the route.
     */
    public void findTrip(String originName, String destinationName, int startTime) {
        UI.clearText();

        // Find the origin and destination stations.
        Station origin = findStationByName(originName);
        Station destination = findStationByName(destinationName);
        if (origin == null || destination == null) {
            UI.println("Can't find a station by that name.");
            return;
        }

        TrainLine tripLine = null;
        TrainService tripService = null;

        // For each train line that goes through the origin:
        for (TrainLine line : origin.getTrainLines()) {
            List<Station> stationsOnLine = line.getStations();

            // Require that the destination must also be on this line.
            if (!stationsOnLine.contains(destination)) continue;

            // Make sure that the destination is after the origin on this line (that we're going the right way)
            if (stationsOnLine.indexOf(origin) > stationsOnLine.indexOf(destination)) continue;

            // For each train service on this line:
            List<TrainService> times = line.getTrainServices()
                    .stream()
                    // Only take the services where they will arrive at the station after the user is ready to start their trip
                    .filter(e -> e.getTimeForStation(origin) > startTime && e.getTimeForStation(origin) != -1)
                    // Sort them by earliest at the station
                    .sorted(Comparator.comparingInt(a -> a.getTimeForStation(origin)))
                    .collect(Collectors.toList());

            // If the new proposed time is quicker than the current "best" time, store it.
            if (!times.isEmpty() && (tripService == null || times.get(0).getTimeForStation(origin) < tripService.getTimeForStation(origin))) {
                tripLine = line;
                tripService = times.get(0);
            }
        }

        if (tripLine != null) {
            List<String> message = new ArrayList<>();
            message.add("Board the " + tripLine.getName() + " train at " + minutesToPrettyTime(dataTimeFormatToMinutes(tripService.getTimeForStation(origin))) + ".");
            message.add("Get off the train at " + destination + " at " + minutesToPrettyTime(dataTimeFormatToMinutes(tripService.getTimeForStation(destination))) + ".");
            message.add("Your journey was " + (dataTimeFormatToMinutes(tripService.getTimeForStation(destination))- dataTimeFormatToMinutes(tripService.getTimeForStation(origin))) + " minutes.");
            message.add(computeFareStatement(Math.abs(origin.getZone()-destination.getZone())));
            JOptionPane.showMessageDialog(UI.getFrame(), String.join("\n", message), "Your route between " + originName + " and " + destinationName, JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            UI.println("I couldn't find a trip between those stations, at that time.");
        }
    }

    /**
     * Finds the nearest stations from a user's click (clickX, clickY) co-ordinates.
     * @param clickX The x-position of the user click.
     * @param clickY The y-position of the user click.
     */
    public void findNearestStationsFromPoint(double clickX, double clickY) {
        UI.clearText();

        UI.println("** Closest stations **");

        // For each station in the database,
        allStations.stream()
                // Calculate its distance from the click point
                .map(station ->
                        Map.entry(station, distance(station.getXCoord(), station.getYCoord(), clickX, clickY))
                )
                // Sort the stream of all stations from smallest to largest
                .sorted(Comparator.comparingDouble(Entry::getValue))
                // Take the closest 10
                .limit(10)
                // And print them.
                .forEach(s -> {
                    UI.println(s.getKey().getName() + ": " + Math.round(s.getValue())/10 + "km");
                });
    }

    /**
     * This function converts the terrible data format syntax "HHMM" or "HMM" to a total number of minutes.
     * For example, 605 (6:05AM) becomes 365 minutes.
     * This makes manipulation and comparison much easier, as minutes can be subtracted without worrying about the base.
     */
    public int dataTimeFormatToMinutes(int input) {
        if (input == -1 || input == 0) return -1;
        String inputString = Integer.toString(input);
        String hours = inputString.length() == 4 ? inputString.substring(0, 2) : String.valueOf(inputString.charAt(0));
        String minutes = inputString.length() == 4 ? inputString.substring(2, 4) : inputString.substring(1,3);
        return (Integer.parseInt(hours)*60) + (Integer.parseInt(minutes));
    }

    /**
     * This function converts a number of minutes (provided normally by dataTimeFormatToMinutes) to a pretty time string.
     * For example, this will convert 1080 minutes (6:00PM) to "6:00 PM".
     */
    public String minutesToPrettyTime(int input) {
        int hours = (int) Math.floor(input/60.0);
        int minutes = (int) input % 60;
        boolean pm = hours >= 12;
        String hoursString = Integer.toString(pm ? (hours == 12 ? hours : hours-12) : (hours == 0 ? 12 : hours));
        String minutesString = minutes >= 10 ? Integer.toString(minutes) : "0" + minutes;
        return hoursString + ":" + minutesString + (pm ? "pm" : "am");
    }

    /**
     * Calculates the distance between two points (ax, ay) and (bx, by).
     */
    public double distance(double ax, double ay, double bx, double by) {
        double diffx = ax-bx;
        double diffy = ay-by;
        return Math.hypot(diffx, diffy);
    }

    /**
     * Finds a possible route between two stations that could be on separate lines.
     * To do this, it finds a route between the origin station and a possible transfer station,
     * and a route between the transfer station and the destination station.
     *
     * It checks that the route starts after startTime, and that all the services are valid and are
     * able to be reached within the time it takes to move between each station.
     * @param originName The origin station.
     * @param destinationName The destination station.
     * @param startTime The time to start the planned route.
     */
    public void findRouteWithPossibleTransfer(String originName, String destinationName, int startTime) {
        UI.clearText();
        Station origin = findStationByName(originName);
        Station destination = findStationByName(destinationName);
        if (origin == null || destination == null) {
            UI.println("Can't find a station with that name.");
            return;
        }

        if (allTrainLines.stream().anyMatch(e -> e.getStations().contains(origin) && e.getStations().contains(destination))) {
            // Both stations are on the same line.
            findTrip(originName, destinationName, startTime);
            return;
        }

        /*
         * General concepts:
         *
         * User starts at origin station (what they input) at startTime.
         *
         * User catches an "origin service" to the "transfer station" (which could be one of many)
         *
         * User catches a "destination service" from the "transfer station" to the "destination station".
         *
         * Complete!
         */

        int nf_bestTimeTrainArrivesAtOriginStation = 0;
        int nf_bestTimeTrainArrivesAtTransferStation = 0;
        int nf_bestTimeTrainDepartsFromTransferStation = 0;
        int nf_bestTimeTrainArrivesAtDestinationStation = 0;
        int bestFzCount = 0;
        TrainService bestOriginService = null;
        TrainService bestDestinationService = null;
        Station bestTransferStation = null;

        for (Station transferStation : allStations) {
            int fzcount = 0;
            TrainLine originConnectionLine = null;
            TrainLine destinationConnectionLine = null;
            Set<TrainLine> linesAtStation = transferStation.getTrainLines();

            // Determine the lines needed to travel to this transfer station from the origin,
            // and to travel from here to the final destination.
            for (TrainLine lineAtStation : linesAtStation) {
                if (lineAtStation.getStations().stream().anyMatch(e -> e == origin && lineAtStation.getStations().indexOf(transferStation) > lineAtStation.getStations().indexOf(origin))) {
                    originConnectionLine = lineAtStation;
                }
                if (lineAtStation.getStations().stream().anyMatch(e -> e == destination  && lineAtStation.getStations().indexOf(destination) > lineAtStation.getStations().indexOf(transferStation))) {
                    destinationConnectionLine = lineAtStation;
                }
            }

            // If we couldn't find one, this can't be a transfer station.
            if (originConnectionLine == null || destinationConnectionLine == null) continue;

            // The train service the user needs to catch from the origin station to the transfer station.
            TrainService originService = null;

            // The time, in minutes, that the origin train service will arrive at the origin station.
            int nf_timeTrainArrivesAtOriginStation = 0;

            // The time, in minutes, that the origin train will arrive at the transfer station.
            int nf_timeTrainArrivesAtTransferStation = 0;

            // The train service that the user needs to catch from the transfer station to the destination station.
            TrainService destinationService = null;

            // The time, in minutes, that the destination train will depart from the transfer station.
            int nf_departureTimeFromTransferStation = 0;

            // The time, in minutes, that the destination train will arrive at the destination station.
            int nf_arrivalTimeAtDestination = 0;

            // For each available service in the origin->transfer train services:
            for (TrainService service : originConnectionLine.getTrainServices()) {
                int timeAtStation = service.getTimeForStation(origin);
                /*
                 * If this service is either the first one to be checked,
                 * OR the current service is impossible (before start time),
                 * OR the current service arrives earlier at the origin service than the current "best" service,
                 *
                 * make it the "best" origin service.
                 */
                if (originService == null || dataTimeFormatToMinutes(originService.getTimeForStation(origin)) < startTime || (originService.getTimeForStation(origin) > timeAtStation && dataTimeFormatToMinutes(timeAtStation) > startTime)) {
                    // Some train services don't go to every station on every service.
                    if (service.getTimeForStation(origin) != -1 && service.getTimeForStation(transferStation) != -1) {
                        originService = service;
                        nf_timeTrainArrivesAtOriginStation = dataTimeFormatToMinutes(timeAtStation);
                        nf_timeTrainArrivesAtTransferStation = dataTimeFormatToMinutes(service.getTimeForStation(transferStation));
                    }
                }
            }
            fzcount += (Math.abs(transferStation.getZone()-origin.getZone()));

            if (originService == null || dataTimeFormatToMinutes(originService.getTimeForStation(origin)) < startTime) continue;

            // For each available service in the transfer->destination train services:
            for (TrainService service : destinationConnectionLine.getTrainServices()) {
                int of_timeDestinationTrainDepartsTransferStation = service.getTimeForStation(transferStation);

                /*
                 * If this service is either the first one to be checked,
                 * OR the current service is impossible (will depart transfer station before the user's origin service can get there),
                 * OR the current service arrives earlier at the origin service than the current "best" service,
                 *
                 * make it the "best" origin service.
                 */
                // If this service is the first one to be checked,
                if (destinationService == null
                        // OR (this train can be taken (departs after the user arrives at transfer)
                        //      AND (this train leaves the transfer station earlier than the current "best" service OR the current service is impossible))
                        //
                        || (
                                dataTimeFormatToMinutes(of_timeDestinationTrainDepartsTransferStation) > nf_timeTrainArrivesAtTransferStation
                                && (dataTimeFormatToMinutes(service.getTimeForStation(transferStation)) < dataTimeFormatToMinutes(destinationService.getTimeForStation(transferStation))
                                || dataTimeFormatToMinutes(destinationService.getTimeForStation(transferStation)) < nf_timeTrainArrivesAtTransferStation)
                        )) {
                    // Some train services don't go to every station on every service.
                    if (service.getTimeForStation(transferStation) != -1 && service.getTimeForStation(destination) != -1) {
                        destinationService = service;
                        nf_departureTimeFromTransferStation = dataTimeFormatToMinutes(destinationService.getTimeForStation(transferStation));
                        nf_arrivalTimeAtDestination = dataTimeFormatToMinutes(destinationService.getTimeForStation(destination));
                    }
                }
            }
            fzcount += (Math.abs(transferStation.getZone()-destination.getZone()));
            if (destinationService != null && destinationService.getTimeForStation(transferStation) > originService.getTimeForStation(transferStation)) {
                if (nf_bestTimeTrainArrivesAtDestinationStation == 0 || nf_arrivalTimeAtDestination < nf_bestTimeTrainArrivesAtDestinationStation) {
                    nf_bestTimeTrainArrivesAtDestinationStation = nf_arrivalTimeAtDestination;
                    nf_bestTimeTrainArrivesAtTransferStation = nf_timeTrainArrivesAtTransferStation;
                    nf_bestTimeTrainDepartsFromTransferStation = nf_departureTimeFromTransferStation;
                    nf_bestTimeTrainArrivesAtOriginStation = nf_timeTrainArrivesAtOriginStation;
                    bestOriginService = originService;
                    bestTransferStation = transferStation;
                    bestDestinationService = destinationService;
                    bestFzCount = fzcount;
                }
            }
        }
        if (bestOriginService != null) {
            List<String> message = new ArrayList<>();
            message.add("Go to " + origin.getName() + " and board the " + bestOriginService.getTrainLine() + " train at " + minutesToPrettyTime(nf_bestTimeTrainArrivesAtOriginStation) + ".");
            message.add("Get off at " + bestTransferStation.getName() + ", at " + minutesToPrettyTime(nf_bestTimeTrainArrivesAtTransferStation) + ".");
            message.add("Wait " + (nf_bestTimeTrainDepartsFromTransferStation-nf_bestTimeTrainArrivesAtTransferStation) + " minutes.");
            message.add("Board the " + bestDestinationService.getTrainLine() + " train at " + minutesToPrettyTime(nf_bestTimeTrainDepartsFromTransferStation) + ".");
            message.add("Get off at " + destination.getName() + " at " + minutesToPrettyTime(nf_bestTimeTrainArrivesAtDestinationStation) + ".");
            message.add("Destination reached. Your journey took " + (nf_bestTimeTrainArrivesAtDestinationStation - startTime) + " minutes.");
            message.add(computeFareStatement(bestFzCount));
            UI.println(String.join("\n", message));
            //JOptionPane.showMessageDialog(UI.getFrame(), String.join("\n", message), "Your route between " + originName + " and " + destinationName, JOptionPane.INFORMATION_MESSAGE);
            highlightStationOnMap(origin,
                    "Start here. Get on the " +
                            bestOriginService.getTrainLine().getStations().get(bestOriginService.getTrainLine().getStations().size()-1).getName()
                            + "-bound train at " + minutesToPrettyTime(nf_bestTimeTrainArrivesAtOriginStation) + ".");
            highlightStationOnMap(bestTransferStation,
                    "Get off here and get on the " +
                            bestDestinationService.getTrainLine().getStations().get(bestOriginService.getTrainLine().getStations().size()-1).getName()
                            + "-bound train at " + minutesToPrettyTime(nf_bestTimeTrainDepartsFromTransferStation) + ".");
            highlightStationOnMap(destination, "Get off at " + bestTransferStation.getName() +" at " + minutesToPrettyTime(nf_bestTimeTrainArrivesAtDestinationStation) + ".");
        }
        else {
            UI.println("I wasn't able to find any possible trips between those stations at that time.");
        }
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        loadTrainLineData();
        loadFares();
        loadedData = true;
    }

    /**
     * Computes a fare statement from a given number of fare zones travelled.
     * @param fareZonesTravelled The number of fare zones travelled.
     * @return A user-friendly string indicating the price and number of fare zones travelled.
     */
    public String computeFareStatement(int fareZonesTravelled) {
        // Being inside in a fare zone adds one, so add one for the origin.
        fareZonesTravelled++;
        if (!fares.containsKey(fareZonesTravelled)) {
            return "You travelled " + fareZonesTravelled + " fare zones.";
        }
        else {
            Double cost = fares.get(fareZonesTravelled);
            return "Your trip will cost $" + cost + " (" + fareZonesTravelled + " zones)";
        }
    }

    /**
     * Re-weights the last component. (Needed for the UI hack to insert the picker boxes).
     * @param inputPanel The input JPanel.
     * @param c The container to re-weight.
     */
    public void reweightLastComponent(JPanel inputPanel, Container c) {
        int ncomps = c.getComponentCount();
        if (ncomps > 0) {
            GridBagLayout lm = (GridBagLayout)c.getLayout();
            Component comp = inputPanel.getComponent(ncomps-1);
            GridBagConstraints lc = lm.getConstraints(comp);
            lc.weighty = 0;
            lm.setConstraints(comp,lc);
        }
    }

    /**
     * Mutates the input panel, and then re-weights and re-draws it.
     * This function can be used to insert new components into the input panel,
     * and is used in setupGUI.
     * @param panelConsumer The mutator function for the input panel.
     */
    public void mutateInputPanel(Consumer<JPanel> panelConsumer) {
        JRootPane root = UI.getFrame().getRootPane();
        JLayeredPane layered = (JLayeredPane) root.getComponents()[1];
        JPanel panel0 = (JPanel) layered.getComponents()[0];
        JPanel panel1 = (JPanel) panel0.getComponents()[0];
        reweightLastComponent(panel1, panel1);

        panelConsumer.accept(panel1);

        panel1.revalidate();
        UI.getFrame().validate();
        UI.getFrame().pack();
    }
    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("List all stations",        this::listAllStations);
        UI.addButton("List all stations by name",    this::listStationsByName);
        UI.addButton("List all lines",           this::listAllTrainLines);

        // Grid constraints for the custom labels
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.insets = new Insets(3,3,0,3);
        labelConstraints.anchor = GridBagConstraints.NORTH;
        labelConstraints.weighty = 1.0;
        labelConstraints.ipady = 2;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;

        // Grid constraints for the custom combo box
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(5,3,5,3);
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 1.0;
        c.ipady = 4;
        c.fill = GridBagConstraints.HORIZONTAL;

        // Create the origin station picker label and combo box
        JLabel stationPickerLabel = new JLabel("Station");
        JComboBox<String> stationPicker = new JComboBox<>(allStations.stream().sorted(Comparator.comparing(Station::getName)).map(Station::getName).toArray(String[]::new));
        this.stationName = stationPicker.getItemAt(0);
        stationPicker.addActionListener((e) -> {
            this.stationName = (String) stationPicker.getSelectedItem();
        });
        mutateInputPanel(panel -> { panel.add(stationPickerLabel, labelConstraints); panel.add(stationPicker, c); });

        // Create the destination station picker label and combo box
        JLabel destinationPickerLabel = new JLabel("Destination");
        JComboBox<String> destinationPicker = new JComboBox<>(allStations.stream().sorted(Comparator.comparing(Station::getName)).map(Station::getName).toArray(String[]::new));
        destinationPicker.addActionListener((e) -> {
            this.destinationName = (String) destinationPicker.getSelectedItem();
        });
        this.destinationName = destinationPicker.getItemAt(0);
        mutateInputPanel(panel -> {
            panel.add(destinationPickerLabel, labelConstraints);
            panel.add(destinationPicker, c);
        });

        // Create the train line picker label and combo box
        JLabel trainLinePickerLabel = new JLabel("Train Line");
        JComboBox<String> linePicker = new JComboBox<>(allTrainLines.stream().map(TrainLine::getName).toArray(String[]::new));
        linePicker.addActionListener((e) -> {
            this.lineName = (String) linePicker.getSelectedItem();
        });
        this.lineName = linePicker.getItemAt(0);
        mutateInputPanel(panel -> {
            panel.add(trainLinePickerLabel, labelConstraints);
            panel.add(linePicker, c);
        });

        // Create the departure time picker label and combo box
        JLabel timePickerLabel = new JLabel("Departure time");
        JComboBox<Map.Entry<String, Integer>> timePicker = new JComboBox<>();

        // Creates a renderer that converts an Map.Entry<String,Integer> into a valid label by accessing the key
        // i.e. { "10pm" => 1320 (minutes) }
        timePicker.setRenderer(new BasicComboBoxRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null) {
                    Map.Entry<String, Integer> item = (Map.Entry<String, Integer>) value;
                    setText(item.getKey());
                } else {
                    setText("");
                }
                return this;
            }
        });
        for (int i = 0; i < 96; i++) {
            timePicker.addItem(Map.entry(minutesToPrettyTime(i*15), i*15));
        }
        timePicker.addActionListener(t -> {
            this.startTime = ((Map.Entry<String, Integer>)timePicker.getSelectedItem()).getValue();
        });
        this.startTime = timePicker.getItemAt(0).getValue();

        mutateInputPanel(panel -> {
            panel.add(timePickerLabel, labelConstraints);
            panel.add(timePicker, c);
        });
        UI.addButton("Lines at station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next services",       () -> {findNextServices(this.stationName, this.startTime);});
        UI.addButton("Find Trip (Same Line)",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});
        UI.addButton("Find Trip (Transfer)", () -> {findRouteWithPossibleTransfer(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (! loadedData){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released") && geographicMap){
            /*# YOUR CODE HERE */
            findNearestStationsFromPoint(x, y);
        }
    }

    // Methods for loading data and answering queries

    /*# YOUR CODE HERE */

}
