package dev.danvega.cg.service;

import dev.danvega.cg.model.GitHubContent;
import dev.danvega.cg.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

/**
 * Service class for interacting with GitHub API and downloading repository contents.
 * This service allows downloading specified file types from a GitHub repository,
 * with support for both include and exclude patterns.
 */
@Service
public class GitHubService {
    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);
    private final PathUtils pathUtils;
    private final RestClient restClient;

    @Value("${github.token}")
    private String token;

    /**
     * Constructs a new GithubService with the specified dependencies.
     *
     * @param builder The RestClient.Builder to use for creating the RestClient.
     */
    public GitHubService(PathUtils pathUtils, RestClient.Builder builder) {
        this.pathUtils = pathUtils;
        this.restClient = builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    /**
     * Downloads the contents of a specified GitHub repository filtered by language patterns
     * and writes them to a file.
     *
     * @param owner The owner of the repository.
     * @param repo The name of the repository.
     * @param includePatterns Patterns for files to include
     * @param excludePatterns Patterns for files to exclude
     * @param outputFilename The name of the output file
     * @throws IOException If an I/O error occurs.
     */
    public void downloadRepositoryContentsForLanguage(
            String owner,
            String repo,
            List<String> includePatterns,
            List<String> excludePatterns,
            String outputFilename) throws IOException {

        StringBuilder contentBuilder = new StringBuilder();

        RestClient authenticatedClient = this.restClient.mutate()
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        downloadContentsRecursively(authenticatedClient, owner, repo, "", contentBuilder, includePatterns, excludePatterns);

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFilename);
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Repository contents written to: {}", outputFile.toAbsolutePath());
    }

    /**
     * Recursively downloads the contents of a repository directory.
     *
     * @param client The authenticated REST client
     * @param owner The owner of the repository.
     * @param repo The name of the repository.
     * @param path The path within the repository to download.
     * @param contentBuilder The StringBuilder to append the content to.
     * @param includePatterns Patterns for files to include
     * @param excludePatterns Patterns for files to exclude
     */
    private void downloadContentsRecursively(
            RestClient client,
            String owner,
            String repo,
            String path,
            StringBuilder contentBuilder,
            List<String> includePatterns,
            List<String> excludePatterns) {

        List<GitHubContent> contents = getRepositoryContents(client, owner, repo, path);

        for (GitHubContent content : contents) {
            if ("file".equals(content.type()) && pathUtils.shouldIncludeFile(content.path(), includePatterns, excludePatterns)) {
                log.info("Downloading file: {}", content.path());
                String fileContent = getFileContent(client, owner, repo, content.path());
                contentBuilder.append("File: ").append(content.path()).append("\n\n");
                contentBuilder.append(fileContent).append("\n\n");
            } else if ("dir".equals(content.type()) && !pathUtils.isExcludedDirectory(content.path(), excludePatterns)) {
                downloadContentsRecursively(client, owner, repo, content.path(), contentBuilder, includePatterns, excludePatterns);
            } else {
                log.debug("Skipping content: {} of type {}", content.path(), content.type());
            }
        }
    }

    /**
     * Retrieves the contents of a repository directory.
     *
     * @param client The authenticated REST client
     * @param owner The owner of the repository.
     * @param repo The name of the repository.
     * @param path The path within the repository to retrieve.
     * @return A list of GitHubContent objects representing the contents of the directory.
     */
    private List<GitHubContent> getRepositoryContents(RestClient client, String owner, String repo, String path) {
        return client.get()
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Retrieves the content of a specific file from the repository.
     *
     * @param client The authenticated REST client
     * @param owner The owner of the repository.
     * @param repo The name of the repository.
     * @param path The path to the file within the repository.
     * @return The content of the file as a String.
     */
    private String getFileContent(RestClient client, String owner, String repo, String path) {
        GitHubContent response = client.get()
                .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                .retrieve()
                .body(GitHubContent.class);
        String cleanedString = response.content().replaceAll("[^A-Za-z0-9+/=]", "");
        return new String(Base64.getDecoder().decode(cleanedString));
    }
}