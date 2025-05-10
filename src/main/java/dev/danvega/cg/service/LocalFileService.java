package dev.danvega.cg.service;

import dev.danvega.cg.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LocalFileService {
    private static final Logger log = LoggerFactory.getLogger(LocalFileService.class);
    private final PathUtils pathUtils;

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

        List<PathMatcher> includeMatchers = pathUtils.createPathMatchers(includePatterns);

        List<PathMatcher> excludeMatchers = pathUtils.createPathMatchers(excludePatterns);

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(file -> pathUtils.shouldIncludeFile(file, sourceDir, includeMatchers, excludeMatchers))
                    .forEach(file -> readFileContent(file, sourceDir, contentBuilder));
        }

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFileName + "." + outputExtension);
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Local directory contents written to: {}", outputFile.toAbsolutePath());
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
}