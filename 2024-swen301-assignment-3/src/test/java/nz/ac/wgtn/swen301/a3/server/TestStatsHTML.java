package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStatsHTML {
    private StatsHTMLServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        Persistency.DB.clear();
        servlet = new StatsHTMLServlet();
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
        Document doc = Jsoup.parse(response.getContentAsString());
        Element table = doc.select("table").get(0);
        Elements rows = table.getElementsByTag("tr");
        Element header = rows.get(0);
        assertEquals("logger", header.getElementsByTag("th").get(0).text());

        assertEquals("3", rows.get(1).getElementsByTag("td").get(Persistency.LEVELS.indexOf("info")+1).text());
        assertEquals("1", rows.get(2).getElementsByTag("td").get(Persistency.LEVELS.indexOf("info")+1).text());
        assertEquals("0", rows.get(1).getElementsByTag("td").get(Persistency.LEVELS.indexOf("warn")+1).text());
    }
}
