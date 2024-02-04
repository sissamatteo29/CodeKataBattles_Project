package org.example.ScoreComputationMicroservice.StaticAnalysisHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResultsFetcher {

    private final String sonarQubeLocalServer = "http://localhost:9000/api";

    @Value("${sonarQube.accessToken}")
    private String sonarAccessToken;

    public double computeStaticAnalysisScore(String username) throws URISyntaxException, IOException, InterruptedException {

        /* Create the HttpRequest and query the server */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest fetchStaticAnalysisResults = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", "Bearer " + sonarAccessToken)
                .uri(new URI(sonarQubeLocalServer + String.format("/measures/component?component=%s&metricKeys=security_rating,reliability_rating", username)))
                .build();
        System.out.println(fetchStaticAnalysisResults.uri());

        List<Double> scores = new ArrayList<>(0);
        /* Repeat the http request because the server might need time to store the new analysis info */
        do {
            Thread.sleep(1000);
            HttpResponse<String> results = client.send(fetchStaticAnalysisResults, HttpResponse.BodyHandlers.ofString());
            System.out.println(results.body());

            /* Use the Jackson library to extract specific fields of the json response */
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(results.body());
            JsonNode measuresNode = rootNode.path("component").path("measures");
            for (JsonNode measure : measuresNode) {
                double value = Double.valueOf(measure.path("value").asText());
                scores.add(value);
            }
        } while(scores.size() == 0);

        /* Return the average between the two values */
        return (scores.get(0) + scores.get(1)) / 2;

    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        ResultsFetcher f = new ResultsFetcher();
        f.computeStaticAnalysisScore("sissa");
    }

}
