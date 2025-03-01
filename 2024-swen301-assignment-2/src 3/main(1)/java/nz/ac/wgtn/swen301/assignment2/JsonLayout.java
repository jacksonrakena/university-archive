package nz.ac.wgtn.swen301.assignment2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

public class JsonLayout extends Layout {
    @Override
    public String format(LoggingEvent loggingEvent) {
        ObjectMapper om = new ObjectMapper();
        StringWriter sw = new StringWriter();
        HashMap<String, String> payload = new HashMap<>();
        payload.put("name", loggingEvent.getLoggerName());
        payload.put("level", loggingEvent.getLevel().toString());
        payload.put("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(loggingEvent.getTimeStamp())));
        payload.put("thread", loggingEvent.getThreadName());
        payload.put("message", loggingEvent.getRenderedMessage());
        try {
            om.writeValue(sw, payload);
            return sw.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {

    }
}
