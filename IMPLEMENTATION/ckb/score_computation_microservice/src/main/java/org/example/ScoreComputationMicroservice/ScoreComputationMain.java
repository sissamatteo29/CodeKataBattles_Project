package org.example.ScoreComputationMicroservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class ScoreComputationMain {

    public static final Path BASE_DIR;

    @Value("${downloadDir}")
    private Path downloadDir;

    static{
        /* Create a customary system property to point to the home directory of the project */
        if(System.getProperty("app.base.dir") == null) {
            System.setProperty("app.base.dir", System.getProperty("user.dir"));
        }
        BASE_DIR = Paths.get(System.getProperty("app.base.dir"));
    }

    public static void main(String[] args) {
        SpringApplication.run(ScoreComputationMain.class, args);
    }
}
