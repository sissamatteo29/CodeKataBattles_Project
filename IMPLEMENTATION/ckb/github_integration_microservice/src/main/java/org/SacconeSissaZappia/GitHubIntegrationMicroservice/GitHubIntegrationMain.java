package org.SacconeSissaZappia.GitHubIntegrationMicroservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

/***
 * Entry point of the GitHubIntegration microservice.
 *
 * When this class is loaded for the first time, the static block
 * sets the BASE_DIR path, which is the absolute path that points to the project home directory.
 * In the development environment the home directory is equivalent to the working directory.
 * All paths in the microservice can now be relative to the BASE_DIR path in order to work on all
 * systems without being dependent from where the application is launched.
 */
@SpringBootApplication
public class GitHubIntegrationMain {

    public static final Path BASE_DIR;


    static{
        /* Create a customary system property to point to the home directory of the project */
        if(System.getProperty("app.base.dir") == null) {
            System.setProperty("app.base.dir", System.getProperty("user.dir"));
        }
        BASE_DIR = Paths.get(System.getProperty("app.base.dir"));
    }

    public static void main(String[] args) {
        SpringApplication.run(GitHubIntegrationMain.class, args);

    }
}
