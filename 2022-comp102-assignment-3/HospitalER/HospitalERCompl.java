// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    // Fields for recording the patients waiting in the waiting room and being treated in the treatment room
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    private Set<Patient> treatedPatients = new HashSet<>();
    public Map<String, Department> departments = new HashMap<>();

    public Map<Integer, Map<Integer, Double>> priorityWaitingTimesByFrame = new HashMap<>();

    public Map<String, List<Double>> occupancyRates = new HashMap<>();

    // fields for the statistics
    /*# YOUR CODE HERE */

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        UI.setDivider(0.3);
        er.reset(false, false);   // initialise with an ordinary queue.
        er.setupGUI();
    }

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false, true); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true, true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
                (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
                (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
                (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
                (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(2000,1000);
    }

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue, boolean sleep){
        running=false;
        if (sleep) UI.sleep(4*delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        treatedPatients = new HashSet<>();


        occupancyRates = new HashMap<>();
        priorityWaitingTimesByFrame = new HashMap<>();

        UI.clearGraphics();
        UI.clearText();

        departments = Map.ofEntries(
                Map.entry("ER", new Department("ER", 5, usePriorityQueue, Color.blue)),
                Map.entry("MRI", new Department("MRI", 5, usePriorityQueue, Color.pink)),
                Map.entry("X-ray", new Department("X-ray", 2, usePriorityQueue, Color.MAGENTA)),
                Map.entry("Ultrasound", new Department("Ultrasound", 1, usePriorityQueue, Color.green)),
                Map.entry("Surgery", new Department("Surgery", 8, usePriorityQueue, Color.LIGHT_GRAY))
        );
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){         // each time step, check whether the simulation should pause.
            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all
            //   the items on the temporary list from the set.

            time++;
            Set<Patient> waitingPatients = new HashSet<>();
            for (Department d : departments.values()) {
                d.statisticsTick();
                var newTreatmentRoom = new HashSet<Patient>();
                for (Patient patientInTreatment : d.treatmentRoom) {
                    if (patientInTreatment.completedCurrentTreatment()) {
                        patientInTreatment.incrementTreatmentNumber();
                        if (patientInTreatment.noMoreTreatments()) {
                            // DISCHARGE
                            UI.println(time + ": " + d.getName() + " Discharge: " + patientInTreatment);
                            treatedPatients.add(patientInTreatment);
                        } else {
                            // MOVE TO NEXT TREATMENT
                            String nextTreatment = patientInTreatment.getCurrentTreatment();
                            Department department = departments.get(nextTreatment);
                            if (department.treatmentRoom.size() < department.getTreatmentRoomSize()) {
                                // GO DIRECTLY TO TREATMENT
                                department.treatmentRoom.add(patientInTreatment);
                                UI.println(time + ": Direct Move " + d.getName() + "->" + department.getName() + " " + patientInTreatment);
                            } else {
                                // GO TO TREATMENT QUEUE
                                department.waitingRoom.add(patientInTreatment);
                                UI.println(time + ": " + d.getName() + " to " + department.getName() + " queue " + patientInTreatment);
                                patientInTreatment.waitForATick();
                                waitingPatients.add(patientInTreatment);
                            }
                        }
                    } else {
                        // CONTINUE TREATMENT
                        patientInTreatment.advanceTreatmentByTick();
                        newTreatmentRoom.add(patientInTreatment);
                    }
                }

                Queue<Patient> newWaitingRoom = d.isPriorityQueueBased() ? new PriorityQueue<>() :  new ArrayDeque<>();
                for (Patient patientInQueue : d.waitingRoom) {
                    if (newTreatmentRoom.size() < d.getTreatmentRoomSize()) {
                        // BEGIN TREATMENT
                        newTreatmentRoom.add(patientInQueue);
                    } else {
                        // CONTINUE WAITING
                        newWaitingRoom.add(patientInQueue);
                        patientInQueue.waitForATick();
                        waitingPatients.add(patientInQueue);
                    }
                }
                d.waitingRoom = newWaitingRoom;
                d.treatmentRoom = newTreatmentRoom;
            }

            // For each department, store its occupancy rate.
            for (String departmentKey : departments.keySet()) {
                if (!occupancyRates.containsKey(departmentKey)) occupancyRates.put(departmentKey, new ArrayList<>());
                occupancyRates.get(departmentKey).add(departments.get(departmentKey).getOccupancyRate());
            }

            // This is an absolutely fucked stream, and I'm sorry

            // For all waiting patients, group them into priority groups,
            // calculating each group's average waiting time, and store that data for this frame.
            priorityWaitingTimesByFrame.put(time, waitingPatients
                    .stream()
                    // Group by priority.
                    .collect(Collectors.groupingBy(Patient::getPriority)).entrySet()
                    // Map each priority group to a map entry,
                    // where the map key is the priority level
                    // and the map value is the average of all waiting times for all patients in this group
                    .stream().map(e ->
                            new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()
                                    .stream()
                                        .mapToInt(Patient::getWaitingTime)
                                        .summaryStatistics().getAverage()
                            )
                    )
                    // Collect the stream of map entries into an actual map object
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            // Get any new patient that has arrived and add them to the waiting room
            if (time==1 || Math.random()<1.0/arrivalInterval){
                Patient newPatient = new Patient(time, randomPriority());
                UI.println(time+ ": Arrived: "+newPatient);
                departments.get(newPatient.getCurrentTreatment()).waitingRoom.offer(newPatient);
            }
            redraw();
            drawChartsAndGraphs();
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        UI.sleep(2);
        var stats = treatedPatients.stream().mapToInt(Patient::getWaitingTime).summaryStatistics();
        UI.println("Number of patients treated: " + stats.getCount());
        UI.println("Average waiting time: " + (int) stats.getAverage() + " ticks");
        UI.println("Longest waiting time: " + (int) stats.getMax() + " ticks");

        var p1stats = treatedPatients.stream().filter(e -> e.getPriority() == 1).mapToInt(Patient::getWaitingTime).summaryStatistics();
        UI.println("Number of P1 patients treated: " + p1stats.getCount());
        UI.println("Average P1 waiting time: " + (int) p1stats.getAverage()+ " ticks");
        UI.println("Longest P1 waiting time: " + (int) p1stats.getMax() + " ticks");

        Map<String, Double> averageOccupancyRates = new HashMap<>();
        for (Map.Entry<String, List<Double>> d : occupancyRates.entrySet()) {
            averageOccupancyRates.put(d.getKey(), Math.floor(d.getValue().stream().mapToDouble(d0 -> d0).average().orElse(0)*100));
            UI.println("Average occupancy rate for " + d.getKey() + ": " + averageOccupancyRates.get(d.getKey())  + "%");
        }

        Optional<Map.Entry<String, Double>> highestRate = averageOccupancyRates.entrySet().stream().max((a, b) -> (int) (a.getValue() - b.getValue()));
        highestRate.ifPresent(stringDoubleEntry -> UI.println(stringDoubleEntry.getKey() + " has the highest occupancy rate, at " + stringDoubleEntry.getValue() + " percent. We should increase the size of the operating room."));

        double ay = 80;
        for (Department d : departments.values()) {
            d.drawStatistics(ay);
            ay+= 50;
        }
        drawChartsAndGraphs();
    }

    public void drawChartsAndGraphs() {
        UI.setFontSize(14);
        // Distribution pie chart
        List<Integer> priorities = treatedPatients.stream().map(Patient::getPriority).collect(Collectors.toList());
        int total = priorities.size();
        int p1 = 0;
        int p2 = 0;
        int p3 = 0;
        for (int p : priorities) {
            if (p == 1) p1++;
            if (p == 2) p2++;
            if (p == 3) p3++;
        }
        double p1Pct = ((double)p1)/total;
        double p2Pct = ((double)p2)/total;
        double p3Pct = ((double)p3)/total;

        // Draw the pie chart showing what portion of the total patients is made up by each priority group
        drawPieChart("Priority distributions", 50, 80+(50*departments.size())+50, 100, 100, 90,
                new PieChartComponent("Priority 1 (" + Math.floor(p1Pct*100) + "%)", Color.red, p1Pct),
                new PieChartComponent("Priority 2 (" + Math.floor(p2Pct*100) + "%)", Color.orange, p2Pct),
                new PieChartComponent("Priority 3 (" + Math.floor(p3Pct*100) + "%)", Color.green, p3Pct));

        // This map holds the distribution of treatments by each priority group
        // So the key is the priority group, and the inner map is
        // a map of names of treatments to the number of that treatment that was performed on this priority group
        Map<Integer, Map<String, Integer>> treatments = new HashMap<>();
        for (Patient p : treatedPatients) {
            if (!treatments.containsKey(p.getPriority())) treatments.put(p.getPriority(), new HashMap<>());
            Map<String, Integer> count = treatments.get(p.getPriority());
            for (String treatment : p.getAllTreatments()) {
                if (!count.containsKey(treatment)) count.put(treatment, 1);
                else count.replace(treatment, count.get(treatment)+1);
            }
        }

        // For each priority group
        for (Integer priorityLevel : treatments.keySet()) {
            Map<String, Integer> distribution = treatments.get(priorityLevel);

            // Get the total number of treatments performed on that group
            int totalNumber = distribution.values().stream()
                    .mapToInt(e -> e).sum();

            // And draw the pie chart
            drawPieChart("Priority " + priorityLevel + " distribution",
                    150+(priorityLevel*150), 80+(50*departments.size())+50, 100, 100, 90,

                    // Map each treatment type to a new PieChartComponent of that treatment type's portion of the overall size
                    distribution.entrySet().stream().map(e -> {
                        double pct = ((double)e.getValue())/(double)totalNumber;
                        return new PieChartComponent(e.getKey() + " (" + Math.floor(pct*100) + "%)",
                                departments.get(e.getKey()).color, pct);
                    }).toArray(PieChartComponent[]::new));
        }

        // Draw the bounds of the line graph
        UI.setColor(Color.black);
        UI.resetLineWidth();
        double graphX = 200;
        double graphY = 80+(50*departments.size())+250;
        double graphHeight = 125;
        double graphWidth = 400;
        UI.drawLine(graphX, graphY, graphX, graphY+graphHeight);
        UI.drawLine(graphX, graphY+graphHeight, graphX+graphWidth, graphY+graphHeight);

        // Figure out the display scale
        double widthScale = graphWidth/(priorityWaitingTimesByFrame.size());
        double largestY = (priorityWaitingTimesByFrame.values().stream().map(Map::values)
                .flatMapToDouble(e -> e.stream().mapToDouble(f -> f)).max().orElse(0));
        double heightScale = graphHeight/largestY;
        UI.drawString("Waiting time over time", graphX+(graphWidth/2)-30, graphY-10);

        // Draw axes indicators
        UI.drawString(Math.floor(largestY) + " ticks", graphX-50, graphY);
        UI.drawString(String.valueOf(0), graphX-20, graphY+graphHeight);
        UI.drawString(String.valueOf(priorityWaitingTimesByFrame.size()), graphX+graphWidth, graphY+graphHeight);

        // For each frame in the recorded data
        for (int i : priorityWaitingTimesByFrame.keySet()) {
            Map<Integer, Double> waitingTimes = priorityWaitingTimesByFrame.get(i);
            if (waitingTimes.keySet().size() == 0) continue;

            // For each priority type
            for (int p = 1; p < 4; p++) {
                Color color = Color.black;
                if (p == 1) color = Color.red;
                if (p == 2) color = Color.orange;
                if (p == 3) color = Color.green;
                UI.setColor(color);
                UI.setLineWidth(2);

                // If there were no patients of this priority type in this frame, the average waiting time was zero
                double waitingTimeForPriority = waitingTimes.containsKey(p) ? waitingTimes.get(p) : 0;
                double previousWaitingTime = 0;

                // If there were patients of this priority type in the last frame, record the Y coordinate,
                // so we can draw a contiguous line
                if (i != 0 && priorityWaitingTimesByFrame.get(i-1).containsKey(p)) {
                    Map<Integer, Double> previousFrame = priorityWaitingTimesByFrame.get(i-1);
                    previousWaitingTime = previousFrame.get(p);
                }

                // Draw the line from the previous Y coordinate to the current waiting time,
                // applying the graph scaling parameters
                UI.drawLine(graphX+((i-1)*widthScale), graphY+graphHeight-(previousWaitingTime*heightScale), graphX+(i*widthScale), graphY+graphHeight-(waitingTimeForPriority*heightScale));
            }
        }
        UI.resetLineWidth();
    }

    /**
     * Represents a component of a pie chart.
     * It contains a name of this portion, a color, and the percentage of the total pie chart.
     */
    class PieChartComponent {
        public String name;
        public Color color;
        public double pct;
        public PieChartComponent(String name, Color color, double pct) {
            this.name = name;
            this.color = color;
            this.pct = pct;
        }
    }

    /**
     * Draws a pie chart from an arbitrary number of pie chart components.
     * It will draw a title, the chart, and a legend of each component and its percentage of the chart.
     * @param caption The name of the chart.
     * @param x The starting left position of the chart.
     * @param y The starting top position of the chart.
     * @param width The width of the chart.
     * @param height The height of the chart.
     * @param startAngle The starting angle of the chart. The first component will start at this position. This is often 90 degrees.
     * @param components All the components to be rendered in the chart.
     */
    public void drawPieChart(String caption, int x, int y, double width, double height, double startAngle, PieChartComponent... components) {
        double operatorAngle = startAngle;
        int textHeight = 18;

        // Draw chart title.
        UI.setColor(Color.black);
        UI.drawString(caption, x, y-15);

        // For each pie chart component
        for (int i = 0; i < components.length; i++) {
            PieChartComponent c = components[i];
            UI.setColor(c.color);

            // Draw its component, starting from the last drawn angle, and draw
            // the portion percentage of a full circle (360 degrees)
            UI.fillArc(x, y, width, height, operatorAngle, c.pct * 360);

            // Draw the component title
            UI.drawString(c.name, x, y + height+ 25 + (i*textHeight));
            operatorAngle += (c.pct * 360);
        }
    }


    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.setColor(Color.black);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        // Draw the treatment room and the waiting room:
        double y = 80;
        UI.setFontSize(14);
        for (Department d : departments.values()) {
            d.redraw(y);
            y+= 50;
        }
        UI.drawLine(0,y+2,400, y+2);
    }

    /**
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }
}
