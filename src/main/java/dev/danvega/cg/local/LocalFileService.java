package dev.danvega.cg.local;

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

    /**
     * Process a local directory and generate a file containing selected files
     * based on the provided language patterns.
     *
     * @param directoryPath     The path to the directory to process
     * @param outputFileName    The base name for the output file
     * @param includePatterns   The patterns for files to include
     * @param excludePatterns   The patterns for files to exclude
     * @param outputExtension   The extension for the output file
     * @throws IOException      If an I/O error occurs
     */
    public void processLocalDirectoryForLanguage(
            String directoryPath,
            String outputFileName,
            List<String> includePatterns,
            List<String> excludePatterns,
            String outputExtension) throws IOException {

        Path sourceDir = Paths.get(directoryPath).normalize().toAbsolutePath();
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        List<PathMatcher> includeMatchers = includePatterns.stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());

        List<PathMatcher> excludeMatchers = excludePatterns.stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern)))
                .collect(Collectors.toList());

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(file -> shouldIncludeFile(file, sourceDir, includeMatchers, excludeMatchers))
                    .forEach(file -> readFileContent(file, sourceDir, contentBuilder));
        }

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFileName + "." + outputExtension);
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Local directory contents written to: {}", outputFile.toAbsolutePath());
    }

    private boolean shouldIncludeFile(
            Path filePath,
            Path sourceDir,
            List<PathMatcher> includeMatchers,
            List<PathMatcher> excludeMatchers) {

        String relativePath = normalizePath(sourceDir.relativize(filePath));
        Path normalizedPath = Paths.get(relativePath);

        for (PathMatcher matcher : excludeMatchers) {
            if (matcher.matches(normalizedPath)) {
                return false;
            }
        }

        if (includeMatchers.isEmpty()) {
            return true;
        }

        for (PathMatcher matcher : includeMatchers) {
            if (matcher.matches(normalizedPath)) {
                return true;
            }
        }

        return false;
    }

    private void readFileContent(Path file, Path sourceDir, StringBuilder builder) {
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