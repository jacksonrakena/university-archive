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
import java.util.*;
import java.util.List;

/**
 * A treatment Department (Surgery, X-ray room,  ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{
    public boolean isPriorityQueueBased() {
        return waitingRoom instanceof PriorityQueue;
    }
    public Set<Patient> treatmentRoom;
    public Queue<Patient> waitingRoom;

    public List<Double> occupancyRateByTick = new ArrayList<>();
    private String name;
    private int maxPatients;

    public Color color;

    public Department(String name, int treatmentRoomSize, boolean priorityBased, Color color) {
        this.name = name;
        this.maxPatients = treatmentRoomSize;
        this.color = color;
        this.treatmentRoom = new HashSet<>();
        this.waitingRoom = priorityBased ? new PriorityQueue<>() : new ArrayDeque<>();
    }

    public String getName() { return this.name; }
    public int getTreatmentRoomSize() { return this.maxPatients;}

    public void statisticsTick() {
        occupancyRateByTick.add(getOccupancyRate());
    }
    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        UI.setColor(Color.BLACK);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
    }

    public void drawStatistics(double y) {
        UI.setFontSize(14);
        UI.setColor(Color.red);
        UI.drawString("Average occupancy rate: " + Math.floor(occupancyRateByTick.stream().mapToDouble(d -> d).average().orElse(0)*100) + "%", name.length()*10, y-35);
        UI.setColor(Color.black);
    }


    /**
     * Returns the occupancy rate (how busy the operating room is) for this department, between 0 and 1.
     */
    public double getOccupancyRate() {
        return ((double)treatmentRoom.size())/this.maxPatients;
    }
}
