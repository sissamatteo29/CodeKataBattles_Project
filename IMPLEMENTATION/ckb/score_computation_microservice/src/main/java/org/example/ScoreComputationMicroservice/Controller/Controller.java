package org.example.ScoreComputationMicroservice.Controller;

import org.example.ScoreComputationMicroservice.CompilerJava;
import org.example.ScoreComputationMicroservice.ScoreComputationMain;
import org.example.ScoreComputationMicroservice.StaticAnalysisHandler.ResultsFetcher;
import org.example.ScoreComputationMicroservice.StaticAnalysisHandler.SonarQubeStaticAnalysisLauncher;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SonarQubeStaticAnalysisLauncher analyzer;

    @Autowired
    private ResultsFetcher resultsFetcher;

    @GetMapping("/computeScore/{username}")
    public int coputeScore(@PathVariable String username, @RequestParam String tour, @RequestParam String battle,
                            @RequestParam String teamName) throws ParseException {
        /* Let's first calculate the absolute path to the local file system where the project is placed */
        Path pathToCode = ScoreComputationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", username, "CODE"));
        System.out.println("The path to the CODE folder to run all the tests is: " + pathToCode);
        HttpClient client = HttpClient.newHttpClient();

        /* Compile the code and get necessary parameters for calculating the score */
        try {
            /* In this list, the first value is the total number of tests run, the second value is the number of tests passed */
            List<Integer> resultTestCases = MavenScriptRunner.runScript(username);
            System.out.println("The result for running the test cases is: " + resultTestCases.toString());

            /* If the compilation fails, the score is automatically 0  and the Score computation microservice communicates it to the Battle microservice */
            if (resultTestCases == null) {
                System.out.println("Result of test cases is null, updating score with 0...");
                String contactBattle = String.format("http://localhost:8083/updateScore?tour=%s&battle=%s&teamName=%s&score=%s", tour, battle, teamName, String.valueOf(0));
                URI url = new URI(contactBattle);
                HttpRequest updateRequest = HttpRequest.newBuilder().uri(url).GET().build();
                client.sendAsync(updateRequest, HttpResponse.BodyHandlers.ofString());
                return 0;
            } else {

                /* Calculate percentage of test cases passed */
                double testCasesPercentage = ((double) resultTestCases.get(1) / resultTestCases.get(0));

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
                String wellFormattedRegDate = regDead.body().substring(1, regDead.body().length() - 1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date regDeadline = dateFormat.parse(wellFormattedRegDate);
                System.out.println("Fetched registration deadline, which is: "+ regDeadline);

                /* Sending and processing second request */
                HttpResponse<String> subDead = client.send(getSubDeadline, HttpResponse.BodyHandlers.ofString());
                String wellFormattedSubDate = subDead.body().substring(1, regDead.body().length() - 1);
                Date subDeadline = dateFormat.parse(wellFormattedSubDate);
                System.out.println("Fetched submission deadline, which is: "+subDeadline);

                /* Let's calculate the difference between the registration deadline and the current moment in seconds */
                // Get the current time as an Instant
                Instant currentTime = Instant.now();
                Instant startOfBattle = regDeadline.toInstant();
                Duration timePassed = Duration.between(startOfBattle, currentTime);
                if (timePassed.getSeconds() < 0) {
                    System.out.println("The battle hasn't started, yet, score set to 0...");
                    /* In this case the battle hasn't started yet, so the score is updated to 0 */
                    String contactBattle = String.format("http://localhost:8083/updateScore?tour=%s&battle=%s&teamName=%s&score=%s", tour, battle, teamName, String.valueOf(0));
                    URI url = new URI(contactBattle);
                    HttpRequest updateRequest = HttpRequest.newBuilder().uri(url).GET().build();
                    client.sendAsync(updateRequest, HttpResponse.BodyHandlers.ofString());
                    return 0;
                } else {

                    /*
                    Let's calculate the contribution of time as a percentage value of the time passed over the total time available for the battle
                    which is the difference between the two deadlines (registration and submission)
                     */
                    Duration totalTimeBattle = Duration.between(regDeadline.toInstant(), subDeadline.toInstant());
                    System.out.println("Total time for the battle is (in seconds): " + totalTimeBattle.getSeconds());

                    /*
                    Since the other two parameters are of type "the higher the better", to make the time variable comply to the same logic, the percentage of
                    the REMAINING AVAILABLE TIME over the total time for the battle is used as the third parameter.
                     */
                    Duration remainingTimeToEnd = Duration.between(Instant.now(), subDeadline.toInstant());
                    double timelinessPercentage = ((double) remainingTimeToEnd.getSeconds() / totalTimeBattle.getSeconds());
                    System.out.println("The percentage assigned for timeliness is: " + timelinessPercentage);


                    /* The last part is for the static analysis tool */
                    /* Let's run the static analysis on the source code */
                    System.out.println("Moving on to launching the static analysis...");
                    analyzer.launchAnalysis(username);
                    /* Now the analyses results are inside the local sonarQube database, it is necessary to extract the result of them */
                    double staticAnalysisScore = resultsFetcher.computeStaticAnalysisScore(username);
                    System.out.println("The score assigned by the static analysis is : " +staticAnalysisScore);

                    double finalScoreOfSolution = calculateFinalPercentage(testCasesPercentage, timelinessPercentage, staticAnalysisScore);
                    System.out.println("The final score before the casting is: " + finalScoreOfSolution);

                    /* Ask battle microservice to update the score of the team */
                    String contactBattle = String.format("http://localhost:8083/updateScore?tour=%s&battle=%s&teamName=%s&score=%s", tour, battle, teamName, String.valueOf((int) finalScoreOfSolution));
                    URI url = new URI(contactBattle);
                    HttpRequest updateRequest = HttpRequest.newBuilder().uri(url).GET().build();
                    System.out.println("Asked to the battle service to update the score for the team"+ teamName);
                    client.sendAsync(updateRequest, HttpResponse.BodyHandlers.ofString());
                    return (int) finalScoreOfSolution;

                }

            }

        } catch (IOException e) {
            System.out.println("IO exception while compiling the java project");
            throw new RuntimeException(e);
        } catch (InterruptedException | URISyntaxException e) {
            System.out.println("Exception while compiling the java project");
            throw new RuntimeException(e);
        }


    }

    private double calculateFinalPercentage(double testCasesPercentage, double timelinessPercentage, double staticAnalysisScore) {
        return ((testCasesPercentage + timelinessPercentage + staticAnalysisScore) / 3) * 100;
    }


}
