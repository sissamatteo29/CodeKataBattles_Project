package org.SacconeSissaZappia.GitHubIntegrationMicroservice.Download;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

/***
 * Simple interface to represent the process of downloading some file from a generic server.
 * Notice that the download method takes as its first parameter the path that has to be appended to the
 * "base URI" of the external restful service in order to download the content.
 */
public interface Downloader {
    public void download(URI internalServerPath, Path localDestinationPath) throws URISyntaxException, IOException, InterruptedException;

}
