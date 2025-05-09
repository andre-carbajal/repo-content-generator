package dev.danvega.cg.local;

import dev.danvega.cg.gh.GitHubConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LocalFileService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileService.class);
    private final GitHubConfiguration config;
    private Path sourceDir;
    private final List<PathMatcher> includeMatchers;
    private final List<PathMatcher> excludeMatchers;

    public LocalFileService(GitHubConfiguration config) {
        this.config = config;
        this.includeMatchers = config.includePatterns().stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());
        this.excludeMatchers = config.excludePatterns().stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());
    }

    public void processLocalDirectory(String directoryPath, String outputFileName) throws IOException {
        sourceDir = Paths.get(directoryPath).normalize().toAbsolutePath();
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .forEach(file -> readFileContent(file, contentBuilder));
        }

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFileName + ".md");
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Local directory contents written to: {}", outputFile.toAbsolutePath());
    }

    public void processLocalDirectoryForJava(String directoryPath, String outputFileName) throws IOException {
        sourceDir = Paths.get(directoryPath).normalize().toAbsolutePath();
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::isJavaFile)
                    .filter(path -> !isExcludedFile(path))
                    .forEach(file -> readFileContent(file, contentBuilder));
        }

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFileName + "-java.txt");
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Local directory Java contents written to: {}", outputFile.toAbsolutePath());
    }

    private boolean shouldIncludeFile(Path filePath) {
        String relativePath = normalizePath(sourceDir.relativize(filePath));
        if (excludeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)))) {
            return false;
        }
        if (includeMatchers.isEmpty()) {
            return true;
        }
        return includeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)));
    }

    private boolean isJavaFile(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        return fileName.endsWith(".java");
    }

    private boolean isExcludedFile(Path filePath) {
        String relativePath = normalizePath(sourceDir.relativize(filePath));
        return excludeMatchers.stream().anyMatch(matcher -> matcher.matches(Paths.get(relativePath)));
    }

    private void readFileContent(Path file, StringBuilder builder) {
        try {
            String relativePath = sourceDir.relativize(file).toString();
            String content = Files.readString(file);
            builder.append("File: ").append(relativePath).append("\n\n")
                    .append(content).append("\n\n");
        } catch (IOException e) {
            log.error("Error reading file: {}", file, e);
        }
    }

    private String normalizePattern(String pattern) {
        return pattern.trim()
                .replace('\\', '/');
    }

    private String normalizePath(Path path) {
        return path.toString().replace('\\', '/');
    }
}