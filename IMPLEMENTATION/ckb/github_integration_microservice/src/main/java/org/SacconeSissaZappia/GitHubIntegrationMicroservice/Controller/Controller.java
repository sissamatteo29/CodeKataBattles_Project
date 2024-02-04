package org.SacconeSissaZappia.GitHubIntegrationMicroservice.Controller;

import org.SacconeSissaZappia.GitHubIntegrationMicroservice.Download.Downloader;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.Git.GitOp;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.GitHubIntegrationMain;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.UnzipUtil.UnzipUtil;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

/***
 * Controller class for the GitHubIntegrationMicroservice, which is responsible for
 * receiving all the requests which require some kind of interaction with the GitHub rest API and
 * handle them.
 * More specifically, this controller manages the creation of GitHub repositories when a battle starts and
 * it coordinates the process for calculating the new score of a student's solution when a commit is performed on GitHub.
 */

@RestController
public class Controller {

    @Value("${githubDownloader.PersonalAccessToken}")
    private String personalAccessToken;

    @Autowired
    private GitOp git;

    @Autowired
    private Downloader downlaoder;



    /***
     * This API method is called by the Gateway Microservice Controller whenever a notification of
     * a new commit arrives from GitHub. This method is the entry point for the entire computation process of the score
     * associated to the new solution uploaded on GitHub.
     * @param username
     * @param repository
     * @return
     */
    @GetMapping("/newSubmission/{username}/{repository}")
    public void downloadNewCode(@PathVariable String username, @PathVariable String repository,
                            @RequestParam String tour, @RequestParam String battle, @RequestParam String teamName) throws URISyntaxException, IOException, InterruptedException {

        /* Download the code from the remote repository */
        URI internalServerPath = new URI(String.format("/repos/%s/%s/zipball", username, repository));
        System.out.println(internalServerPath);
        downlaoder.download(internalServerPath, Path.of(username+ ".zip"));

        /* Unzip the content of the directory */
        Path localRepoPath = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("code_download", username + ".zip"));
        System.out.println(localRepoPath);
        UnzipUtil.unzipFileFromTo(localRepoPath, null);
        /* Rename directory with username of the committer */
        UnzipUtil.renameDirectory(localRepoPath.getParent(), username, username);
        /* Remove zip file */
        localRepoPath.toFile().delete();

        /* Contact the Score Computation Microservice to calculate the new score and update it in the DB */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(String.format("http://localhost:8089/computeScore/%s?tour=%s&battle=%s&teamName=%s",
                        username, tour, battle, teamName)))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.discarding());


    }

    @GetMapping("/publishRepo/{tournament}/{battle}")
    public void createGitHubRepo(@PathVariable String tournament, @PathVariable String battle) throws URISyntaxException, IOException, InterruptedException, GitAPIException {
        /* Create a new local repository for the battle to be created (inside the dedicated local folder "battles" */
        Path localPathToBattle = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("battles", battle));
        localPathToBattle.toFile().mkdirs();

        /* Get the build automation scripts and test cases from the battle microservice (through API calls) */
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest testCasesRequest = HttpRequest.newBuilder()
                .GET().uri(new URI(String.format("http://localhost:8083/getTests/%s/%s", tournament, battle)))
                .build();
        HttpRequest buildAutomationScriptsRequest = HttpRequest.newBuilder()
                .GET().uri(new URI(String.format("http://localhost:8083/getScripts/%s/%s", tournament, battle)))
                .build();
        HttpResponse<Path> getTests = client.send(testCasesRequest, HttpResponse.BodyHandlers.ofFile(localPathToBattle.resolve("tests.zip")));
        HttpResponse<Path> getScripts = client.send(buildAutomationScriptsRequest, HttpResponse.BodyHandlers.ofFile(localPathToBattle.resolve("scripts.zip")));

        /* Extract the two zip files */
        UnzipUtil.unzipFileFromTo(localPathToBattle.resolve("tests.zip"), null);
        UnzipUtil.unzipFileFromTo(localPathToBattle.resolve("scripts.zip"), null);

        /* Remove zip files */
        localPathToBattle.resolve("tests.zip").toFile().delete();
        localPathToBattle.resolve("scripts.zip").toFile().delete();


        /* Create the empty directory on Github with the name of the battle */
        HttpRequest newRepo = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.format("{ \"name\": \"%s\" }", battle)))
                .uri(new URI(String.format("https://api.github.com/user/repos")))
                .header("Authorization", "Bearer " + personalAccessToken)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(newRepo, HttpResponse.BodyHandlers.ofString());

        /* Associate local repository to remote repository and push all the content */
        URIish uri = new URIish(String.format("https://github.com/ckbGitHub/%s.git", battle));
        git.performGitOperations(localPathToBattle, uri);
    }





}
