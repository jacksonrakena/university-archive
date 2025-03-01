package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.wgtn.swen301.a3.data.LogEvent;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

@WebServlet("/logs")
public class LogsServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String limitString = request.getParameter("limit");
        String leveltemp = request.getParameter("level");
        if (limitString == null || leveltemp == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing limit or level parameter");
            return;
        }
        String level = leveltemp.toLowerCase();
        if (Persistency.LEVELS.stream().noneMatch(e -> e.equals(level))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid level");
            return;
        }

        try {
            int limit = Integer.parseUnsignedInt(limitString);

            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(Persistency.DB.stream()
                    .filter(e -> Persistency.LEVELS.indexOf(e.level().toLowerCase()) >= Persistency.LEVELS.indexOf(level)).sorted(Comparator.comparing(LogEvent::timestamp).reversed()).limit(limit).toList()));
        } catch (NumberFormatException n) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid limit");
        }

    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String input = request.getReader().lines().collect(Collectors.joining());
        try {
            LogEvent ev = new ObjectMapper().readValue(input, LogEvent.class);
            if (ev.level().equalsIgnoreCase("all") || ev.level().equalsIgnoreCase("off")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "cannot assign a new event as 'all' or 'off' level");
                return;
            }
            if (Persistency.LEVELS.stream().noneMatch(e -> e.equals(ev.level()))) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid level");
                return;
            }
            if (Persistency.DB.stream().anyMatch(e -> e.id().equals(ev.id()))) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "id already exists");
                return;
            }
            if (ev.timestamp() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid timestamp");
                return;
            }
            Persistency.DB.add(ev);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (JsonParseException | JsonMappingException ex) {
            System.out.println(ex.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Persistency.DB.clear();
        response.setStatus(HttpServletResponse.SC_OK);
    }
}