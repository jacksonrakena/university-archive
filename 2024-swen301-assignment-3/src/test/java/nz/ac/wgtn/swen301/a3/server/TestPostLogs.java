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
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPostLogs {
    private LogsServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        Persistency.DB.clear();
        servlet = new LogsServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    private LogEvent[] fetchEvents(String level, int count) throws IOException {
        MockHttpServletRequest fetchRequest = new MockHttpServletRequest();
        MockHttpServletResponse fetchResponse = new MockHttpServletResponse();
        fetchRequest.addParameter("limit", Integer.toString(count));
        fetchRequest.addParameter("level", level);
        servlet.doGet(fetchRequest, fetchResponse);
        return new ObjectMapper().readValue(fetchResponse.getContentAsString(), LogEvent[].class);
    }

    public MockHttpServletResponse tryInsertLogEvent(LogEvent ev) throws IOException {
        var insertRequest = new MockHttpServletRequest();
        var insertResponse = new MockHttpServletResponse();
        insertRequest.setContent(new ObjectMapper().writeValueAsBytes(ev));
        servlet.doPost(insertRequest, insertResponse);
        return insertResponse;
    }

    @Test
    public void testCannotInsertMultipleLogsWithSameId() throws IOException {
        LogEvent e = new LogEvent(UUID.randomUUID(), "", Date.from(Instant.now()), "", "", "info", "");
        var firstInsert = tryInsertLogEvent(e);
        assertEquals(HttpServletResponse.SC_CREATED, firstInsert.getStatus());
        var secondInsert = tryInsertLogEvent(e);
        assertEquals(HttpServletResponse.SC_CONFLICT, secondInsert.getStatus());
    }

    @Test
    public void testCannotInsertInvalidLevel() throws IOException {
        LogEvent e = new LogEvent(UUID.randomUUID(), "", Date.from(Instant.now()), "", "", "cheese", "");
        var firstInsert = tryInsertLogEvent(e);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, firstInsert.getStatus());
    }

    @Test
    public void testCorrectInsertion() throws IOException {
        LogEvent e = new LogEvent(UUID.randomUUID(), "Hello", Date.from(Instant.now()), "main", "cheese", "warn", "");
        var firstInsert = tryInsertLogEvent(e);
        assertEquals(HttpServletResponse.SC_CREATED, firstInsert.getStatus());
    }

    @Test
    public void testCorrectOrderedRetrieval() throws IOException {
        Instant key = Instant.now();
        tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "1", Date.from(key.plusSeconds(1)), "main", "cheese", "warn", "")
        );
        tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "2", Date.from(key.plusSeconds(2)), "main", "cheese", "warn", "")
        );
        tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "3", Date.from(key.plusSeconds(3)), "main", "cheese", "warn", "")
        );
        tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "4", Date.from(key.plusSeconds(4)), "main", "cheese", "warn", "")
        );
        tryInsertLogEvent(
                new LogEvent(UUID.randomUUID(), "5", Date.from(key.plusSeconds(5)), "main", "cheese", "warn", "")
        );
        LogEvent[] events = fetchEvents("warn", 3);
        assertEquals(3, events.length);
        assertEquals("5", events[0].message());
        assertEquals("4", events[1].message());
        assertEquals("3", events[2].message());
    }
}
