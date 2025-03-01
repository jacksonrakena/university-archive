package nz.ac.wgtn.swen301.assignment2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class JsonLayoutTest {
    MemoryAppender appender;
    JsonLayout layout;
    Logger logger;

    @BeforeEach
    public void setUp() {
        appender = new MemoryAppender();
        layout = new JsonLayout();
        appender.setLayout(layout);
        logger = Logger.getLogger(JsonLayoutTest.class);
        Logger.getRootLogger().removeAllAppenders();
        logger.addAppender(appender);
    }

    @Test
    public void testJsonParseable() throws IOException {
        Instant now = Instant.now();
        logger.info("hello world!");
        String output = layout.format(appender.getCurrentLogs().get(0));
        ObjectMapper om = new ObjectMapper();
        JsonNode node = om.readTree(output);
        assertEquals("hello world!", node.get("message").textValue());
        assertEquals("INFO", node.get("level").textValue());
        assertEquals("main", node.get("thread").textValue());
        assertEquals("nz.ac.wgtn.swen301.assignment2.JsonLayoutTest", node.get("name").textValue());
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(node.get("timestamp").textValue());
        assertTrue(Duration.between(now, Instant.from(ta)).toMillis() < 100, "Duration between logged timestamp and pre-log timestamp is " + Duration.between(now, Instant.from(ta)).toMillis() + " ms");
    }

    @Test
    public void testDoesNotIgnoreThrowable() {
        assertFalse(layout.ignoresThrowable());
    }
}
