package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubService;
import dev.danvega.cg.local.LocalFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ContentGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorService.class);
    private final GitHubService ghService;
    private final LocalFileService localFileService;
    @Value("${app.output.directory}")
    private String outputDirectory;

    public ContentGeneratorService(GitHubService ghService, LocalFileService localFileService) {
        this.ghService = ghService;
        this.localFileService = localFileService;
    }

    public String generateContent(String githubUrl, String localPath) throws Exception {
        if (githubUrl != null && !githubUrl.isBlank()) {
            log.info("Processing GitHub URL: {}", githubUrl);
            String[] parts = githubUrl.split("/");
            String owner = parts[parts.length - 2];
            String repo = parts[parts.length - 1];
            ghService.downloadRepositoryContents(owner, repo);
            return new String(Files.readAllBytes(Paths.get(outputDirectory, repo + ".md")));
        } else if (localPath != null && !localPath.isBlank()) {
            log.info("Processing local path: {}", localPath);
            String outputName = Paths.get(localPath).getFileName().toString();
            localFileService.processLocalDirectory(localPath, outputName);
            return new String(Files.readAllBytes(Paths.get(outputDirectory, outputName + ".md")));
        } else {
            throw new IllegalArgumentException("Either GitHub URL or local path must be provided");
        }
    }

    public String generateJavaContent(String githubUrl, String localPath) throws Exception {
        if (githubUrl != null && !githubUrl.isBlank()) {
            log.info("Processing GitHub URL for Java content: {}", githubUrl);
            String[] parts = githubUrl.split("/");
            String owner = parts[parts.length - 2];
            String repo = parts[parts.length - 1];
            ghService.downloadRepositoryJavaContents(owner, repo);
            return new String(Files.readAllBytes(Paths.get(outputDirectory, repo + "-java.txt")));
        } else if (localPath != null && !localPath.isBlank()) {
            log.info("Processing local path for Java content: {}", localPath);
            String outputName = Paths.get(localPath).getFileName().toString();
            localFileService.processLocalDirectoryForJava(localPath, outputName);
            return new String(Files.readAllBytes(Paths.get(outputDirectory, outputName + "-java.txt")));
        } else {
            throw new IllegalArgumentException("Either GitHub URL or local path must be provided");
        }
    }
}