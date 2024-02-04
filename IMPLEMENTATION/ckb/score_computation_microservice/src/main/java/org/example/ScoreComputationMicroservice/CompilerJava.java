package org.example.ScoreComputationMicroservice;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CompilerJava {
        public static boolean compileJavaProject(Path sourceDirectory, String mainClass) throws IOException, InterruptedException {
            // Build the javac command
            String javaHome = System.getProperty("java.home");
            System.out.println(javaHome);
            Path javacPath = Paths.get(javaHome, "bin", "javac");
            System.out.println(javacPath);

            List<String> command = List.of(
                    javacPath.toString(),
                    mainClass
            );

            // Execute the command using ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .directory(sourceDirectory.toFile());

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("Compilation of code failed");
                return false;
            } else {
                return true;
            }
        }

        public static void main(String[] args) {
            try {
                // Example usage:
                Path sourceDirectory = Paths.get("C:\\Users\\matte\\Desktop\\Masters\\First_Year\\Software_Engineering_2\\CodeKataBattles_Project\\IMPLEMENTATION\\ckb\\code_download\\sissamatteo29\\code");

                compileJavaProject(sourceDirectory, "Main.java");
                System.out.println("Java project compiled successfully.");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

