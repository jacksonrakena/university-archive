package nz.ac.wgtn.swen301.a3.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;

import java.io.IOException;

@WebServlet("/stats/html")
public class StatsHTMLServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder b = new StringBuilder();
        b.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<body>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>logger</th>\n");
        for (String level : Persistency.LEVELS.stream().map(String::toUpperCase).toList()) {
            b.append("<th>").append(level).append("</th>\n");
        }
        b.append("</tr>\n");
        for (String lname : Persistency.DB.stream().map(LogEvent::logger).distinct().toList()) {
            b.append("<tr>\n");
            b.append("<td>\n").append(lname).append("</td>\n");

            for (int i = 0; i < Persistency.LEVELS.size(); i++) {
                String level = Persistency.LEVELS.get(i);
                b.append("<td>");
                b.append(Persistency.DB.stream().filter(e -> e.logger().equals(lname) && e.level().equals(level)).count());
                b.append("</td>");
            }
            b.append("</tr>\n");
        }
        b.append("</table></body></html>");
        response.setStatus(200);
        response.getWriter().write(b.toString());
    }
}
