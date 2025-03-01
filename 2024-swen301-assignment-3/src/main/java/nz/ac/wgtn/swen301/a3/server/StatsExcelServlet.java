package nz.ac.wgtn.swen301.a3.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

@WebServlet("/stats/excel")
public class StatsExcelServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Log Stats");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("logger");

        var results = Persistency.getLoggerStats();
        List<String> levels = Persistency.LEVELS.stream().map(String::toUpperCase).toList();
        for (int i = 0; i < levels.size(); i++) {
            header.createCell(i + 1).setCellValue(levels.get(i));
        }
        int rowIndex = 1;
        for (String logger : results.keySet()) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(logger);
            for (int i = 0; i < results.get(logger).length; i++) {
                row.createCell(i+1).setCellValue(results.get(logger)[i]);
            }

            rowIndex++;
        }

        response.setStatus(200);
        workbook.write(response.getOutputStream());
    }
}
