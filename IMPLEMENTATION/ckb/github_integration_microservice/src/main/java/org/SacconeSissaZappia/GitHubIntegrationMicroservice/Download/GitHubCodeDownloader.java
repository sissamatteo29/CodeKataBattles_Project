package org.SacconeSissaZappia.GitHubIntegrationMicroservice.Download;

import org.SacconeSissaZappia.GitHubIntegrationMicroservice.GitHubIntegrationMain;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.UnzipUtil.UnzipUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.net.URI;

/***
 * Implementation of the Downloader interface to handle the download of files (GitHub repositories and source code)
 * from GitHub, through the GitHub rest API at https://api.github.com.
 */
@Service
public class GitHubCodeDownloader implements Downloader {

    private HttpClient githubClient;
    @Value("${githubDownloader.PersonalAccessToken}")
    private String personalAccessToken;
    @Value("${githubDownloader.baseURL}")
    private URI baseURL;

    /* Constructor showing the dependency on HttpClient */
    public GitHubCodeDownloader(HttpClient githubClient) {
        this.githubClient = githubClient;
    }

    @Override
    public void download(URI internalServerPath, Path localDestinationPath) throws URISyntaxException, IOException, InterruptedException {

        /* Resolve the URL in order to create the complete http request for the server to be contacted */
        URI completeURL = baseURL.resolve(internalServerPath);

        /* In order to make the application flexible, the path on the local file system in which the download of the file will be carried out
        is built starting from the BASE DIR (home directory of the project obtained by external user environment), resolved with the
        downloadDirectory (field injected, it is the path to the directory dedicated to the download inside the home project folder), resolved
        with the localDestinationPath that is basically the path internal do the download folder where the file will be placed. */
        Path absoluteLocalPath = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download")).resolve(localDestinationPath);
        System.out.println("The complete URL sent to github is: "+completeURL);
        System.out.println("The BASE_DIR is set to: "+  GitHubIntegrationMain.BASE_DIR);
        System.out.println("The absolute path in which the zip is going to be downloaded is: "+ absoluteLocalPath);
        if (!absoluteLocalPath.getParent().toFile().exists()) {
            absoluteLocalPath.getParent().toFile().mkdirs();
        }

        /* Build the HttpRequest to send to GitHub */
        HttpRequest downloadRequest = HttpRequest.newBuilder()
                .GET()
                .uri(completeURL)
                .header("Authorization", "Bearer" + personalAccessToken)
                .build();

        /* Send the request and download the file in the localDestinationPath */
        HttpResponse<Path> downloadResponse = githubClient.send(downloadRequest,
                responseInfo -> {
                    if (responseInfo.statusCode() == 200)
                        return HttpResponse.BodySubscribers.ofFile(absoluteLocalPath);
                    else throw new RuntimeException();
                });

        System.out.println("finished reading the response");

    }




    public static void main(String[] args) {
        ApplicationContext ap = SpringApplication.run(GitHubIntegrationMain.class, args);

        System.out.println(System.getProperty("user.name"));

        GitHubCodeDownloader dn = ap.getBean(GitHubCodeDownloader.class);
        try {
            dn.download(new URI("/repos/sissamatteo29/LearnHaskell/zipball"), Path.of("sissamatteo29.zip"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Path downloadDir = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("code_download"));
        Path absolutePath = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("code_download")).resolve(Path.of("sissamatteo29.zip"));

        try {
            UnzipUtil.unzipFileFromTo(absolutePath, absolutePath.getParent());
            UnzipUtil.renameDirectory(downloadDir, "sissamatteo29", "sissamatteo29");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}


