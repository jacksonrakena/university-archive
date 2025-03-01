package nz.ac.wgtn.swen301.assignment2.example;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LogRunner {
    public static void main(String[] args) throws InterruptedException {
        final Logger l = Logger.getLogger(LogRunner.class.getName());
        long timeToRunMillis = 2 * 60 * 1000;
        long endTime = System.currentTimeMillis() + timeToRunMillis;
        while (System.currentTimeMillis() < endTime) {
            Priority p = Priority.getAllPossiblePriorities()[(int)Math.floor(Math.random()*Priority.getAllPossiblePriorities().length)];
            l.log(p, "This is a message logged at " + Instant.now().toEpochMilli());

            Thread.sleep(1000);
        }
    }
}
