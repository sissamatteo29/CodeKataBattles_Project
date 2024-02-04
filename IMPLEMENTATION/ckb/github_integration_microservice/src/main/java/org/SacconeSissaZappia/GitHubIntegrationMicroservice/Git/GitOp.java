package org.SacconeSissaZappia.GitHubIntegrationMicroservice.Git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class GitOp {

    @Value("${githubDownloader.PersonalAccessToken}")
    private String personalAccessToken;

   /* public static void main(String[] args) {
        String localDirectoryPath = "/path/to/local/repository";
        String githubRepoUrl = "https://github.com/username/repo.git";
        String username = "yourGitHubUsername";
        String password = "yourGitHubPassword";

        try {
            performGitOperations(localDirectoryPath, githubRepoUrl, username, password);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }*/

    public void performGitOperations(Path localDirectoryPath, URIish githubRepoUrl)
            throws IOException, GitAPIException {

        // Initialize the Git repository
        Git git = initializeGitRepository(localDirectoryPath);

        // Add the remote GitHub repository
        addRemote(git, "origin", githubRepoUrl);

        // Add all files to the staging area
        git.add().addFilepattern(".").call();

        // Commit the changes
        git.commit().setMessage("Initial commit").call();

        // Push changes to the remote repository
        pushToRemote(git, "main");
        System.out.println("Pushed to github the entire directory content");

        // Close the Git repository
        git.close();
    }

    private Git initializeGitRepository(Path localDirectoryPath) throws IOException, GitAPIException {
        // Create a Git repository in the specified directory
        Git git = Git.init().setDirectory(localDirectoryPath.toFile()).call();
        System.out.println("initialized git repo");

        return git;
    }

    private void addRemote(Git git, String remoteName, URIish remoteUrl) throws GitAPIException {
        git.remoteAdd()
                .setName(remoteName)
                .setUri(remoteUrl)
                .call();
    }

    private void pushToRemote(Git git, String branchName) {
        // Set up credentials for GitHub authentication
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("token", personalAccessToken);

        // Push changes to the remote repository
        try {
            git.push()
                    .setRemote("origin")
                    .setCredentialsProvider(credentialsProvider)
                    .setRefSpecs(new RefSpec("refs/heads/" + branchName + ":refs/heads/" + branchName))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}

