package nz.ac.wgtn.swen301.a3.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        String type = args[0];
        String path = args[1];
        if (!type.equals("csv") && !type.equals("excel")) {
            throw new RuntimeException("invalid format");
        }
        var client = HttpClient.newHttpClient();
        client.send(HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/logstore/stats/"+type)).build(),
                HttpResponse.BodyHandlers.ofFile(Path.of(path)));
        System.out.println("Done");
    }
}
