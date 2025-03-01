package nz.ac.wgtn.swen301.a3.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetLogs {
    private LogsServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        servlet = new LogsServlet();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testMissingLimit() throws IOException {
        request.addParameter("level", "warn");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testMissingLevel() throws IOException {
        request.addParameter("limit", "20");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testInvalidLevel() throws IOException {
        request.addParameter("level", "spaghetti");
        request.addParameter("limit", "20");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testInvalidLimit() throws IOException {
        request.addParameter("level", "warn");
        request.addParameter("limit", "-1");

        servlet.doGet(request, response);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testValidResponse() throws IOException {
        var postRequest = new MockHttpServletRequest();
        var postResponse = new MockHttpServletResponse();

        var om = new ObjectMapper();
        LogEvent test = new LogEvent(UUID.randomUUID(), "main", Date.from(Instant.now()), "main", "test", "warn", "bingus");

        System.out.println("Creating:\n" + om.writeValueAsString(test));
        postRequest.setContent(om.writeValueAsBytes(test));
        servlet.doPost(postRequest, postResponse);
        assertEquals(HttpServletResponse.SC_CREATED, postResponse.getStatus());

        request.addParameter("level", "info");
        request.addParameter("limit", "1");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        LogEvent[] resultant = om.readValue(response.getContentAsString(), LogEvent[].class);

        System.out.println("Received:\n" + response.getContentAsString());

        assertEquals("application/json", response.getContentType());
        assertEquals(test, resultant[0]);
    }
}
