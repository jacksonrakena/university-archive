package nz.ac.wgtn.swen301.a3.server;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;

import java.io.IOException;

@WebServlet("/stats/csv")
public class StatsCSVServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder b = new StringBuilder();
        b.append("logger\t");
        b.append(String.join("\t", Persistency.LEVELS.stream().map(String::toUpperCase).toList()));
        b.append("\n");
        for (String lname : Persistency.DB.stream().map(LogEvent::logger).distinct().toList()) {
            b.append(lname).append("\t");
            for (int i = 0; i < Persistency.LEVELS.size(); i++) {
                String level = Persistency.LEVELS.get(i);
                b.append(Persistency.DB.stream().filter(e -> e.logger().equals(lname) && e.level().equals(level)).count());
                if (i != Persistency.LEVELS.size() - 1) {
                    b.append("\t");
                }
            }
            b.append("\n");
        }
        response.setStatus(200);
        response.getWriter().write(b.toString());
    }
}
