package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentManagerApp {

    // THE FOLLOWING METHOD MUST BE IMPLEMENTED
    /**
     * Executable: the user will provide argument(s) and print details to the console as described in the assignment brief,
     * E.g. a user could invoke this by running "java -cp <someclasspath> <arguments></arguments>"
     * @param arg
     */
    public static void main (String[] arg) throws ParseException {
        Options options = new Options();
        options.addOption("s", "select", true, "Fetches a student record by ID.");
        options.addOption("a", "all", false, "Fetches all student records.");
        options.addOption("e", "export", true, "Fetches all student records and exports them to a given file.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, arg);
        var sm = new StudentManager();

        if (cmd.hasOption("s")) {
            try {
                Student s = StudentManager.fetchStudent(cmd.getOptionValue("s"));
                System.out.println("ID: " + s.getId() + " | First name: " + s.getFirstName() + " | Last name: " + s.getName());
            } catch (NoSuchRecordException e) {
                System.out.println(e.getMessage());
            }
        }
        else if (cmd.hasOption("a")) {
            for (Student s : StudentManager.fetchAllStudents()) {
                System.out.println("ID: " + s.getId() + " | First name: " + s.getFirstName() + " | Last name: " + s.getName());
            }
        }
        else if (cmd.hasOption("e")) {
            String filename = cmd.getOptionValue("e");
            List<String[]> lines = new ArrayList<>();
            lines.add(new String[] { "id", "first_name", "name", "degree" });
            for (Student s : StudentManager.fetchAllStudents()) {
                lines.add(new String[] { s.getId(), s.getFirstName(), s.getName(), s.getDegree().getId() });
            }

            dumpCsv(filename, lines);
        }
    }

    /**
     * MOST OF THE CODE BELOW WAS COPIED FROM https://www.baeldung.com/java-csv
     */

    public static void dumpCsv(String filename, List<String[]> lines) {
        File csvOutputFile = new File(filename);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            lines.stream()
                    .map(d -> Stream.of(d)
                            .map(StudentManagerApp::escapeSpecialCharacters)
                            .collect(Collectors.joining(",")))
                    .forEach(pw::println);
            System.out.println("Written to " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to write");
        }
    }

    public static String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
