// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2022T1, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import ecs100.UI;
import ecs100.UIFileChooser;

/**
 * WeatherReporter
 * Analyses weather data from files of weather-station measurements.
 *
 * The weather data files consist of a set of measurements from weather stations
 * around
 * New Zealand at a series of date/time stamps.
 * For each date/time, the file has:
 * A line with the date and time (four integers for day, month, year, and time)
 * eg "24 01 2021 1900" for 24 Jan 2021 at 19:00
 * A line with the number of weather-stations for that date/time
 * Followed by a line of data for each weather station:
 * - name: one token, eg "Cape-Reinga"
 * - (x, y) coordinates on the map: two numbers, eg 186 38
 * - four numbers for temperature, dew-point, suface-pressure, and
 * sea-level-pressure
 * Some of the data files (eg hot-weather.txt, and cold-weather.txt) have data
 * for just one date/time.
 * The weather-all.txt has data for lots of times. The date/times are all in
 * order.
 * You should look at the files before trying to complete the methods below.
 *
 * Note, the data files were extracted from MetOffice weather data from 24-26
 * January 2021
 */

// Represents a single weather station record, which looks like this:
// Mokohinau-Islands 251 89 19 15.9 1004.9 1013.1
class StationRecord {
    public final String name;
    public final LocalDateTime date;
    public final double locationX;
    public final double locationY;
    public final double temperature;
    public final double dewPoint;
    public final double surfacePressure;
    public final double seaLevelPressure;

    private StationRecord(String name, LocalDateTime date, double locationX, double locationY,
            double temp, double dewPoint, double surfacePressure, double seaLevelPressure) {
        this.name = name;
        this.locationX = locationX;
        this.date = date;
        this.locationY = locationY;
        this.temperature = temp;
        this.dewPoint = dewPoint;
        this.surfacePressure = surfacePressure;
        this.seaLevelPressure = seaLevelPressure;
    }

    public static StationRecord parse(Scanner scanner, LocalDateTime date) {
        String name = scanner.next();
        double locationX = scanner.nextDouble();
        double locationY = scanner.nextDouble();
        double temp = scanner.nextDouble();
        double dewPoint = scanner.nextDouble();
        double surfacePressure = scanner.nextDouble();
        double seaLevelPressure = scanner.nextDouble();
        return new StationRecord(name, date, locationX, locationY, temp, dewPoint, surfacePressure,
                seaLevelPressure);
    }
}

/**
 * Represents a weather snapshot, which is a list
 * of associated StationRecord objects, with a date and time.
 */
class WeatherSnapshot {
    public final LocalDateTime time;
    public final StationRecord[] records;

    public static WeatherSnapshot parse(Scanner input) {
        String[] timeInput = input.nextLine().split("\\s+");

        // This is a little trick to pad out times that aren't exactly four letters
        // long, so it works better with the DateTimeFormatter.
        if (timeInput[3].length() != 4) {
            int n = 4 - timeInput[3].length();
            timeInput[3] = new String(new char[n]).replace("\0", "0") + timeInput[3];
        }
        LocalDateTime time = LocalDateTime.parse(String.join(" ", timeInput),
                DateTimeFormatter.ofPattern("dd MM yyyy HHmm"));
        int numRecords = Integer.parseInt(input.nextLine());

        StationRecord[] records = new StationRecord[numRecords];
        for (int i = 0; i < numRecords; i++) {
            records[i] = StationRecord.parse(input, time);
            input.nextLine();
        }
        return new WeatherSnapshot(time, records);
    }

    private WeatherSnapshot(LocalDateTime time, StationRecord[] records) {
        this.time = time;
        this.records = records;
    }
}

public class WeatherReporter {

    public static final double DIAM = 10; // The diameter of the temperature circles.
    public static final double LEFT_TEXT = 10; // The left of the date text
    public static final double TOP_TEXT = 50; // The top of the date text

    public final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * COMPLETION
     * Plots the temperatures for one date/time from a file on a map of NZ
     * Asks for the name of the file and opens a Scanner
     * It is good design to call plotSnapshot, passing the Scanner as an argument.
     */
    public void plotTemperatures() {
        UI.clearPanes();
        UI.setDivider(0.1);
        try {
            Scanner input = new Scanner(Path.of(UIFileChooser.open("Weather data:")));
            WeatherSnapshot snapshot = WeatherSnapshot.parse(input);
            plotSnapshot(snapshot);
        } catch (IOException e) {
            UI.println("File reading failed");
        }
    }

    /**
     * COMPLETION:
     * Plot the temperatures for the next snapshot in the file by drawing
     * a filled coloured circle (size DIAM) at each weather-station location.
     * The colour of the circle should indicate the temperature.
     *
     * The method should
     * - read the date/time and draw the date/time at the top-left of the map.
     * - read the number of stations, then
     * - for each station,
     * - read the name, coordinates, and data, and
     * - plot the temperature for that station.
     * (Hint: You will find the getTemperatureColor(...) method useful.)
     *
     * CHALLENGE:
     * Also finds the highest and lowest temperatures at that time, and
     * plots them with a larger circle.
     * (Hint: If more than one station has the highest (or coolest) temperature,
     * you only need to draw a larger circle for one of them.
     */
    public void plotSnapshot(WeatherSnapshot snapshot) {
        UI.clearGraphics();
        UI.drawImage("map-new-zealand.gif", 0, 0);

        // title
        UI.setColor(Color.black);
        UI.drawString(DATE_FORMAT.format(snapshot.time) + " at " + TIME_FORMAT.format(snapshot.time), LEFT_TEXT,
                TOP_TEXT);

        StationRecord max = calculateMaxTemperature(snapshot.records);
        StationRecord min = calculateMinTemperature(snapshot.records);
        for (StationRecord record : snapshot.records) {
            double x = record.locationX;
            double y = record.locationY;
            UI.setColor(getTemperatureColor(record.temperature));

            double diam = record.name.equals(max.name) || record.name.equals(min.name) ? DIAM * 2 : DIAM;
            UI.fillOval(x - diam / 2, y - diam / 2, diam, diam);
        }
    }

    public StationRecord calculateMaxTemperature(StationRecord[] records) {
        StationRecord max = records[0];
        for (StationRecord record : records) {
            if (record.temperature > max.temperature)
                max = record;
        }
        return max;
    }

    public StationRecord calculateMinTemperature(StationRecord[] records) {
        StationRecord min = records[0];
        for (StationRecord record : records) {
            if (record.temperature < min.temperature)
                min = record;
        }
        return min;
    }

    /**
     * This method parses an arbitrarily-long list of strings into a list of
     * snapshots,
     * with no demarcation (the parser will use the specified number of records to
     * calculate
     * where the next snapshot starts)
     */
    public List<WeatherSnapshot> parseSnapshots(Scanner input) {
        List<WeatherSnapshot> snapshots = new ArrayList<>();
        input.next()
        boolean parse = true;
        while (parse) {
            if (!input.hasNext()) {
                parse = false;
                break;
            }

            WeatherSnapshot snapshot = WeatherSnapshot.parse(input);
            snapshots.add(snapshot);
        }
        return snapshots;
    }

    /**
     * CHALLENGE
     * Displays an animated view of the temperatures over all
     * the times in a weather data files, plotting the temperatures
     * for the first date/time, as in the completion, pausing for half a second,
     * then plotting the temperatures for the second date/time, and
     * repeating until all the data in the file has been shown.
     * 
     * (Hint, use the plotSnapshot(...) method that you used in the completion)
     */
    public void animateTemperatures() {
        UI.clearPanes();
        UI.setDivider(0.1);
        try {
            List<WeatherSnapshot> snapshots = parseSnapshots(new Scanner(Path.of(UIFileChooser.open("Weather data:"))));

            for (int i = 0; i < snapshots.size(); i++) {
                WeatherSnapshot snapshot = snapshots.get(i);
                plotSnapshot(snapshot);
                UI.sleep(500);
            }

        } catch (IOException e) {
            UI.println("File reading failed");
        }
    }

    public StationRecord findRecordInSnapshot(String stationName, WeatherSnapshot snapshot) {
        for (int i = 0; i < snapshot.records.length; i++) {
            if (snapshot.records[i].name.toLowerCase().equals(stationName.toLowerCase())) {
                return snapshot.records[i];
            }
        }
        return null;
    }

    /**
     * CHALLENGE
     * Prints a table of all the weather data from a single station, one line for
     * each day/time.
     * Asks for the name of the station.
     * Prints a header line
     * Then for each line of data for that station in the weather-all.txt file, it
     * prints
     * a line with the date/time, temperature, dew-point, surface-pressure, and
     * sealevel-pressure
     * If there are no entries for that station, it will print a message saying
     * "Station not found".
     * Hint, the \t in a String is the tab character, which helps to make the table
     * line up.
     */
    public void reportStation() {
        UI.clearPanes();
        UI.setDivider(0.5);
        String stationName = UI.askString("Name of a station: ");

        try {
            List<WeatherSnapshot> snapshots = parseSnapshots(new Scanner(Path.of("weather-all.txt")));

            List<StationRecord> records = snapshots.stream().map(e -> findRecordInSnapshot(stationName, e))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (records.size() == 0) {
                UI.println("Unknown station.");
                return;
            }

            UI.printf("Report for %s: \n", stationName);
            UI.println("Date       \tTime \tTemp \tdew \tkPa \t\tsea kPa");
            for (StationRecord record : records) {
                UI.print(record.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\t");
                UI.print(record.date.format(DateTimeFormatter.ofPattern("HH:mm")) + "\t");
                UI.print(record.temperature + "\t");
                UI.print(record.dewPoint + "\t");
                UI.print(record.surfacePressure + "\t\t");
                UI.print(record.seaLevelPressure);
                UI.println();
            }

        } catch (IOException e) {
            UI.println("File reading failed");
        }

    }

    /**
     * Returns a color representing that temperature
     * The colors are increasingly blue below 15 degrees, and
     * increasingly red above 15 degrees.
     */
    public Color getTemperatureColor(double temp) {
        double max = 37, min = -5, mid = (max + min) / 2;
        if (temp < min || temp > max) {
            return Color.white;
        } else if (temp <= mid) { // blue range: hues from .7 to .5
            double tempFracOfRange = (temp - min) / (mid - min);
            double hue = 0.7 - tempFracOfRange * (0.7 - 0.5);
            return Color.getHSBColor((float) hue, 1.0F, 1.0F);
        } else { // red range: .15 to 0.0
            double tempFracOfRange = (temp - mid) / (max - mid);
            double hue = 0.15 - tempFracOfRange * (0.15 - 0.0);
            return Color.getHSBColor((float) hue, 1.0F, 1.0F);
        }
    }

    public void setupGUI() {
        UI.initialise();
        UI.addButton("Plot temperature", this::plotTemperatures);
        UI.addButton("Animate temperature", this::animateTemperatures);
        UI.addButton("Report", this::reportStation);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(800, 750);
        UI.setFontSize(18);
    }

    public static void main(String[] arguments) {
        WeatherReporter obj = new WeatherReporter();
        obj.setupGUI();
    }

}
