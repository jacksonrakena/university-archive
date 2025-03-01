package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStatsCSV {
    private StatsCSVServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        Persistency.DB.clear();
        servlet = new StatsCSVServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    public MockHttpServletResponse tryInsertLogEvent(LogEvent ev) throws IOException {
        var insertRequest = new MockHttpServletRequest();
        var insertResponse = new MockHttpServletResponse();
        insertRequest.setContent(new ObjectMapper().writeValueAsBytes(ev));
        new LogsServlet().doPost(insertRequest, insertResponse);
        return insertResponse;
    }

    @Test
    public void testCorrectRetrieval() throws IOException {
        assertEquals(HttpServletResponse.SC_CREATED, tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "hello", Date.from(Instant.now()), "", "first", "info", "")
        ).getStatus());
        assertEquals(HttpServletResponse.SC_CREATED, tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "hello", Date.from(Instant.now()), "", "first", "info", "")
        ).getStatus());
        assertEquals(HttpServletResponse.SC_CREATED, tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "hello", Date.from(Instant.now()), "", "first", "info", "")
        ).getStatus());
        assertEquals(HttpServletResponse.SC_CREATED, tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "hello", Date.from(Instant.now()), "", "second", "info", "")
        ).getStatus());

        servlet.doGet(request, response);
        String[] lines = response.getContentAsString().split("\n");
        Map<String, String[]> comps = new HashMap<>();
        for (String reporterLine : Arrays.stream(lines).skip(1).toList()) {
            String[] components = reporterLine.split("\t");
            comps.put(components[0], Arrays.stream(components).skip(1).toArray(String[]::new));
        }
        assertEquals(3, Integer.parseUnsignedInt(comps.get("first")[Persistency.LEVELS.indexOf("info")]));
        assertEquals(1, Integer.parseUnsignedInt(comps.get("second")[Persistency.LEVELS.indexOf("info")]));
        assertEquals(0, Integer.parseUnsignedInt(comps.get("first")[Persistency.LEVELS.indexOf("warn")]));
    }
}
