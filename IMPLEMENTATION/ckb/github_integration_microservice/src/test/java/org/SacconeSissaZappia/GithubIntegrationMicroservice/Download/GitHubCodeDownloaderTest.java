package org.SacconeSissaZappia.GithubIntegrationMicroservice.Download;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.Download.GitHubCodeDownloader;
import org.SacconeSissaZappia.GitHubIntegrationMicroservice.GitHubIntegrationMain;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

/***
 * Test class to verify that the GitHubCodeDownloader is able to download from an external link
 * a file and place it in the destination path specified in its download method.
 * This test makes use of WireMock to mock the behavior of the GitHub external API and in this way
 * isolate the test class from external components.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GitHubCodeDownloaderTest {
    @Autowired
    private GitHubCodeDownloader downloader;

    private WireMockServer wireMockServer;

    @Test
    public void testDownloadCodeGitHub() throws URISyntaxException, IOException, InterruptedException {

        wireMockServer = new WireMockServer(WireMockConfiguration.options().usingFilesUnderDirectory("github_integration_microservice/src/test/resources"));
        wireMockServer.start();
        System.out.println("Started wiremock server");

        /* Configuration of the wireMockServer to reply to the GitHubCodeDownloader instance */
        stubFor(get(urlEqualTo("/testDownload"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/zip")
                        .withBodyFile("test_download.zip")));


        Path localDestinationPath = Path.of("test_download.zip");
        URI internalServerPath = new URI("/testDownload");

        /* Let's use the GitHubCodeDownloader instance to fetch the file from the mock server */
        downloader.download(internalServerPath, localDestinationPath);

        Path absoluteTestPath = GitHubIntegrationMain.BASE_DIR.resolve(Path.of("github_integration_microservice", "code_download", "test_download.zip"));

        /* As a first assertion, let's verify the file exists in the correct directory */
        assertTrue(absoluteTestPath.toFile().exists());

        /* Let's test if the files inside are present as expected */
        ZipInputStream zip = new ZipInputStream(new FileInputStream(absoluteTestPath.toFile()));
        ZipEntry entry = zip.getNextEntry();
        assertNotNull(entry);

        String[] expectedFiles = {"archive/README.md", "archive/Texts/", "archive/Texts/TextMessage.txt"};
        Set<String> foundFiles = new HashSet<>();
        while (entry != null) {
            foundFiles.add(entry.getName());
            entry = zip.getNextEntry();
        }
        assertEquals(Set.of(expectedFiles), foundFiles);

    }

    @AfterClass
    public static void cleanDirectory(){

    }
}

