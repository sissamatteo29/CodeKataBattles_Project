package org.example.ScoreComputationMicroservice.ScriptHandler;

import org.example.ScoreComputationMicroservice.ScoreComputationMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Utility class that runs the build scripts for a project downloaded from Github.
 */

public class MavenScriptRunner {

    public static List<Integer> runScript(String username) throws InterruptedException {

        List<Integer> result = new ArrayList<>();

        Path pathToScript;
        /* Build the path to the build scripts, different depending on the operating system on which this project is running */
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            pathToScript = ScoreComputationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", username, "SCRIPTS", "WINDOWS", "build.bat"));
        } else {
            pathToScript = ScoreComputationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", username, "SCRIPTS", "UNIX-LIKE", "build.sh"));
        }

        Path pathToMavenProject = ScoreComputationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", username, "CODE"));

        try {
            /* Run the build script with a ProcessBuilder */
            ProcessBuilder processBuilder = new ProcessBuilder(pathToScript.toString());
            processBuilder.directory(pathToMavenProject.toFile()); // Set the working directory to point at the CODE folder where the project resides
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            System.out.println("Executing the script...");
            /* Wait for the process to complete */
            //process.waitFor();
            //System.out.println("Execution of the script terminated!");

            /* Check if the script execution was successful */
            /* Read the output of the script */
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            /*
            When Maven reports the number of test cases passed, it uses the writing "Tests run: n, Failures: m"
             */
            Pattern pattern = Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("Results")) {
                    /* Read all lines until you reach the one you need */
                    while (!line.contains("Tests run:")) {
                        System.out.println(line);
                        line = reader.readLine();
                    }
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        // Extract the values using group indices
                        int totalTests = Integer.parseInt(matcher.group(1));
                        result.add(totalTests);
                        int failures = Integer.parseInt(matcher.group(2));
                        /* Add to the result list the number of test cases PASSED */
                        result.add(totalTests - failures);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

}