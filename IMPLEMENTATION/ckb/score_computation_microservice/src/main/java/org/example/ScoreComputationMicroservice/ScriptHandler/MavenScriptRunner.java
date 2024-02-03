package org.example.ScoreComputationMicroservice.ScriptHandler;

import org.example.ScoreComputationMicroservice.ScoreComputationMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Utility class that runs the build scripts for a project downloaded from Github.
 */

public class MavenScriptRunner {

    public static List<Integer> runScript(String username) {

        List<Integer> result = new ArrayList<>(2);

        Path pathToScript;
        /* Build the path to the build scripts, different depending on the operating system on which this project is running */
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            pathToScript =  ScoreComputationMain.BASE_DIR.resolve(Path.of("code_download", username, "SCRIPTS", "WINDOWS", "build.bat"));
        } else {
            pathToScript =  ScoreComputationMain.BASE_DIR.resolve(Path.of("code_download", username, "SCRIPTS", "UNIX-LIKE", "build.sh"));
        }

        Path pathToMavenProject = ScoreComputationMain.BASE_DIR.resolve(Path.of("code_download", username, "CODE"));

        try {
            /* Run the build script with a ProcessBuilder */
            ProcessBuilder processBuilder = new ProcessBuilder(pathToScript.toString());
            processBuilder.directory(pathToMavenProject.toFile()); // Set the working directory to point at the CODE folder where the project resides
            Process process = processBuilder.start();

            /* Wait for the process to complete */
            int exitCode = process.waitFor();

            /* Check if the script execution was successful */
            if (exitCode == 0) {
                /* Read the output of the script */
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                /*
                When Maven reports the number of test cases passed, it uses the writing "Tests run: n, Failures: m"
                 */
                Pattern pattern = Pattern.compile("Tests run: (\\d+), Failures: (\\d+), Errors: (\\d+), Skipped: (\\d+)");
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Results")) {
                        /* Read other two lines */
                        reader.readLine();
                        line = reader.readLine();
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            // Extract the values using group indices
                            int totalTests = Integer.parseInt(matcher.group(1));
                            result.add(totalTests);
                            int failures = Integer.parseInt(matcher.group(2));
                            result.add(totalTests - failures);
                            return result;
                        }
                    }
                }
            } else {
                System.err.println("Script execution failed with exit code: " + exitCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}