package org.example.ScoreComputationMicroservice.Controller;

import org.example.ScoreComputationMicroservice.CompilerJava;
import org.example.ScoreComputationMicroservice.ScoreComputationMain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.example.ScoreComputationMicroservice.ScriptHandler.MavenScriptRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@RestController
public class Controller {

    @Value("${downloadDir}")
    private Path downloadDir;

    @GetMapping("/computeScore/{username}")
    public void coputeScore(@PathVariable String username, @RequestParam String tour, @RequestParam String battle,
                            @RequestParam String teamName) throws ParseException {
        /* Let's first calculate the absolute path to the local file system where the project is placed */
        Path pathToCode = ScoreComputationMain.BASE_DIR.resolve(downloadDir).resolve(Path.of(username, "CODE"));
        System.out.println(pathToCode);
        HttpClient client = HttpClient.newHttpClient();

        /* Compile the code and get necessary parameters for calculating the score */
        try {
            List<Integer> resultTestCases = MavenScriptRunner.runScript(username);
            System.out.println(resultTestCases.toString());

            /* If the compilation fails, the score is automatically 0  and the Score computation microservice communicates it to the Battle microservice */
            if(resultTestCases == null) {
                String contactBattle = String.format("http://localhost:8083/updateScore?tour=%s&battle=%s&teamName=%s&score=%s", tour, battle, teamName, String.valueOf(0));
                URI url = new URI(contactBattle);
                HttpRequest updateRequest =  HttpRequest.newBuilder().uri(url).GET().build();
                client.sendAsync(updateRequest, HttpResponse.BodyHandlers.ofString());
            } else {

                /* Move on calculating the other parameters for the score, the first one is the time passed from the regDeadline of the battle */
                HttpRequest getRegDeadline = HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI(String.format("http://localhost:8083/getRegDeadline/%s/%s", tour, battle)))
                        .build();

                HttpRequest getSubDeadline = HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI(String.format("http://localhost:8083/getSubDeadline/%s/%s", tour, battle)))
                        .build();

                /* Sending and processing first request */
                HttpResponse<String> regDead = client.send(getRegDeadline, HttpResponse.BodyHandlers.ofString());
                String wellFormattedRegDate = regDead.body().substring(1,regDead.body().length()-1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date regDeadline = dateFormat.parse(wellFormattedRegDate);
                System.out.println(regDeadline);

                /* Sending and processing second request */
                HttpResponse<String> subDead = client.send(getSubDeadline, HttpResponse.BodyHandlers.ofString());
                String wellFormattedSubDate = subDead.body().substring(1,regDead.body().length()-1);
                Date subDeadline = dateFormat.parse(wellFormattedSubDate);
                System.out.println(subDeadline);

                /* Let's calculate the difference between the registration deadline and the current moment in seconds */
                // Get the current time as an Instant
                Instant currentTime = Instant.now();
                Instant startOfBattle = regDeadline.toInstant();
                Duration duration = Duration.between(startOfBattle, currentTime);
                // Print the difference in seconds
                System.out.println("Time difference in seconds: " + duration.getSeconds());



            }

        } catch (IOException e) {
            System.out.println("IO exception while compiling the java project");
            throw new RuntimeException(e);
        } catch (InterruptedException | URISyntaxException e) {
            System.out.println("Exception while compiling the java project");
            throw new RuntimeException(e);
        }


    }




}
