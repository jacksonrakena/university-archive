package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestStatsXLS {
    private StatsExcelServlet servlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setup() {
        Persistency.DB.clear();
        servlet = new StatsExcelServlet();
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
        XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
        XSSFSheet mainSheet = wb.getSheet("Log Stats");
        assertNotNull(mainSheet);

        assertEquals("logger", mainSheet.getRow(0).getCell(0).getStringCellValue());

        List<String> readLoggers = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            Row r = mainSheet.getRow(i);
            assertNotNull(r, "expected row " + i + " to exist");
            readLoggers.add(r.getCell(0).getStringCellValue());
        }

        assertEquals(3, mainSheet.getRow(1+readLoggers.indexOf("first")).getCell(Persistency.LEVELS.indexOf("info")+1).getNumericCellValue());
        assertEquals(1, mainSheet.getRow(1+readLoggers.indexOf("second")).getCell(Persistency.LEVELS.indexOf("info")+1).getNumericCellValue());
        assertEquals(0, mainSheet.getRow(1+readLoggers.indexOf("first")).getCell(Persistency.LEVELS.indexOf("warn")+1).getNumericCellValue());
    }
}
