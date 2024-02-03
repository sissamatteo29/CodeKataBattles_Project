package org.SacconeSissaZappia.GitHubIntegrationMicroservice;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class GetContent {

/*
    public static void main(String[] args) {



       HttpRequest getRepoRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/alessandrosaccone/SacconeSissaZappia/contents"))
                .header("Accept", "application/vnd.github.raw+json")
                .headers("Authorization", "Bearer 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")
                .build();

        try {
            HttpResponse<String> response = client.send(getRepoRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            System.out.println(response.headers());
            System.out.println(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        HttpRequest downloadRepo = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/alessandrosaccone/SacconeSissaZappia/zipball/main"))
                .header("Authorization", "Bearer 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")
                .build();

        try {
            HttpResponse<Path> resp = client.send(downloadRepo, HttpResponse.BodyHandlers.ofFile(Paths.get("C:\\Users\\matte\\Desktop\\Spring_App\\download.zip")));
            System.out.println(resp);
            System.out.println(resp.headers());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }*/
}
