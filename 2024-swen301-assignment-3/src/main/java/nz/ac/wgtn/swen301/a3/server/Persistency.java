package nz.ac.wgtn.swen301.a3.server;

import nz.ac.wgtn.swen301.a3.data.LogEvent;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Persistency {
    public static List<LogEvent> DB = new ArrayList<>();
    public static final List<String> LEVELS = List.of(
            "all",
            "trace",
            "debug",
            "info",
            "warn",
            "error",
            "fatal",
            "off"
    );

    public static Map<String, Long[]> getLoggerStats() {
        Map<String, Long[]> m = new HashMap<>();
        for (String logger : DB.stream().map(LogEvent::logger).distinct().toList()) {
            Long[] l = new Long[LEVELS.size()];
            for (int i = 0; i < LEVELS.size(); i++) {
                int finalI = i;
                l[i] = DB.stream().filter(e -> e.level().equals(LEVELS.get(finalI)) && e.logger().equals(logger)).count();
            }
            m.put(logger, l);
        }
        return m;
    }
}
