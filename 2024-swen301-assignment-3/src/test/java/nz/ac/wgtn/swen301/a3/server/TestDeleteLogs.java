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

public class TestDeleteLogs {
    private LogsServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
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

    @Test
    public void testDeletesAllLogs() throws IOException {
        var om = new ObjectMapper();
        for (int i = 0; i < 10; i++) {
            LogEvent test = new LogEvent(UUID.randomUUID(), "main", Date.from(Instant.now()), "main", "test", "warn", "bingus");
            MockHttpServletRequest createRequest = new MockHttpServletRequest();
            MockHttpServletResponse createResponse = new MockHttpServletResponse();
            createRequest.setContent(om.writeValueAsBytes(test));
            servlet.doPost(createRequest, createResponse);
            assertEquals(HttpServletResponse.SC_CREATED, createResponse.getStatus());
        }

        LogEvent[] events = fetchEvents("warn", 9);
        assertEquals(9, events.length);
        System.out.println("Inserted " + events.length + " events");

        servlet.doDelete(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        LogEvent[] afterEvents = fetchEvents("warn", 9);
        assertEquals(0, afterEvents.length);
        System.out.println("Deleted all events");
    }
}
