package org.example.ScoreComputationMicroservice.StaticAnalysisHandler;

import org.example.ScoreComputationMicroservice.ScoreComputationMain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

/***
 * This class handles the creation of a separate process on the local machine to launch the sonar-scanner
 * which is responsible for analyzing the code and sending the results to the local instance of the web server
<<<<<<< HEAD
 * sonarQube, by default listening at http://localhost:9000
 * As the projectKey to create a new project on the sonarQube web server, the username of the user performing the commit
 * on Github is used. In this way a new analysis is run for all the commits and commits coming from the same user will override previous
=======
 * sonarQube, by default listening at "http://localhost:9000"
 * As the projectKey to create a new project on the sonarQube web server, the username of the user performing the commit
 * on GitHub is used. In this way a new analysis is run for all the commits and commits coming from the same user will override previous
>>>>>>> matte
 * analysis.
 *
 */
@Service
public class SonarQubeStaticAnalysisLauncher {

    @Value("${sonarQube.accessToken}")
    private String sonarAccessToken;

    public void launchAnalysis(String username) throws IOException, InterruptedException {

        /* Based on the operating system, the command for launching the shell is different */
        String os = System.getProperty("os.name").toLowerCase();
        String commandShell = os.contains("win") ? "cmd" : "/bin/bash";
        String sonarScannerCommand = String.format("sonar-scanner -D sonar.projectKey=%s -D sonar.host.url=http://localhost:9000 -D sonar.token=%s -D sonar.java.sources=src/main/java -D sonar.java.binaries=target/classes",
                username, sonarAccessToken);

        System.out.println("The complete command launched on the terminal is: " + sonarScannerCommand);

        // Run the SonarScanner command with a ProcessBuilder
        ProcessBuilder analyzeSourceCode;
        if (os.contains("win")) {
            analyzeSourceCode = new ProcessBuilder(commandShell, "/c", sonarScannerCommand);
            System.out.println("recognized windows");
        } else {
            analyzeSourceCode = new ProcessBuilder(commandShell, "-c", sonarScannerCommand);
        }
        System.out.println(analyzeSourceCode.command());

        /* Set the directory to the CODE directory for the specified user */
        Path pathToProjectUser = ScoreComputationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", username, "CODE"));
        System.out.println("The path to the project to be analyzed is: " + pathToProjectUser);
        analyzeSourceCode.directory(pathToProjectUser.toFile());

        /* Launch the new process */
        analyzeSourceCode.redirectErrorStream(true);
        Process analyzeProcess = analyzeSourceCode.start();

        // Read and display the output
        try (InputStream inputStream = analyzeProcess.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = analyzeProcess.waitFor();
        System.out.println("sonar-scanner exit with code: " + exitCode);


    }

    public static void main(String[] args) {
        try {
            new SonarQubeStaticAnalysisLauncher().launchAnalysis("sissa");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
