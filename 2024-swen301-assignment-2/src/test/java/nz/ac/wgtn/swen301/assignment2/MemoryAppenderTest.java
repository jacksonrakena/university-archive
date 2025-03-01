package nz.ac.wgtn.swen301.assignment2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryAppenderTest {
    MemoryAppender appender;
    Logger logger;
    @BeforeEach
    public void setUp() {
        appender = new MemoryAppender();
        logger = Logger.getLogger(MemoryAppenderTest.class);
        Logger.getRootLogger().removeAllAppenders();
        logger.addAppender(appender);
    }

    static List<Priority> logLevels = Arrays.asList(Priority.getAllPossiblePriorities());

    @ParameterizedTest
    @FieldSource("logLevels")
    public void testAppenderStoresLogEvent(Priority level) {
        logger.log(level, "Hello world!");

        assertEquals(1, appender.getCurrentLogs().size());
        assertEquals(level.toString(), appender.getCurrentLogs().get(0).getLevel().toString());
    }

    @Test
    public void testDefaultAppenderSize() {
        assertEquals(1000, appender.getMaxSize());
    }

    @Test
    public void testChangeAppenderSize() {
        appender.setMaxSize(600);
        assertEquals(600, appender.getMaxSize());
    }

    @Test
    public void testAppenderCollection() {
        for (int i = 0; i < 1500; i++) {
            logger.info(Integer.toString(i));
        }
        assertEquals(1000, appender.getCurrentLogs().size());
        assertEquals("500", appender.getCurrentLogs().get(0).getRenderedMessage());
        assertEquals(500, appender.getDiscardedLogCount());
    }

    @Test
    public void testAppenderCollectsAfterResize() {
        for (int i = 0; i < 1000; i++) {
            logger.info(Integer.toString(i));
        }
        appender.setMaxSize(300);

        assertEquals(300, appender.getCurrentLogs().size());
        assertEquals("700", appender.getCurrentLogs().get(0).getRenderedMessage());
        assertEquals(700, appender.getDiscardedLogCount());
    }

    @Test
    public void testDoesNotRequireLayout() {
        assertFalse(appender.requiresLayout());
    }

    @Test
    public void testExport() throws IOException {
        for (int i = 0; i < 1000; i++) {
            logger.info(Integer.toString(i));
        }
        Files.deleteIfExists(Path.of("test.json"));
        appender.export("test.json");

        String input = Files.readString(Path.of("test.json"));
        JsonNode jsonNode = new ObjectMapper().readTree(input);
        assertTrue(jsonNode.isArray());

        assertEquals(1000, jsonNode.size());
        assertEquals("0", jsonNode.get(0).get("message").textValue());
        assertEquals("999", jsonNode.get(999).get("message").textValue());
    }

    @Test
    public void testClose() {
        appender.close();
    }

    @Test
    public void testBeanFunctionality() {
        for (int i = 0; i < 1000; i++) {
            logger.info(Integer.toString(i));
        }
        assertEquals(appender.getCurrentLogs().size(), appender.getLogs().length);
        assertEquals(appender.getLogCount(), appender.getCurrentLogs().size());
    }
}
