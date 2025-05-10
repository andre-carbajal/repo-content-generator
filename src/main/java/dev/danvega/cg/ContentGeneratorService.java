package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubService;
import dev.danvega.cg.local.LocalFileService;
import dev.danvega.cg.processor.LanguageProcessor;
import dev.danvega.cg.processor.LanguageProcessorRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ContentGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorService.class);
    private final GitHubService ghService;
    private final LocalFileService localFileService;
    private final LanguageProcessorRegistry processorRegistry;

    @Value("${app.output.directory}")
    private String outputDirectory;

    public String generateContent(String githubUrl, String localPath, String languageType) throws Exception {
        LanguageProcessor processor = processorRegistry.getProcessor(languageType)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language type: " + languageType +
                        ". Supported types: " + String.join(", ", processorRegistry.getSupportedLanguages())));

        log.info("Processing with language type: {}", languageType);

        String outputName = getFilename(githubUrl, localPath);
        String outputFilename = outputName + "." + processor.getOutputExtension();

        if (githubUrl != null && !githubUrl.isBlank()) {
            log.info("Processing GitHub URL: {}", githubUrl);
            String[] parts = githubUrl.split("/");
            String owner = parts[parts.length - 2];
            String repo = parts[parts.length - 1];

            ghService.downloadRepositoryContentsForLanguage(
                    owner,
                    repo,
                    processor.getIncludePatterns(),
                    processor.getExcludePatterns(),
                    outputFilename
            );

            return new String(Files.readAllBytes(Paths.get(outputDirectory, outputFilename)));
        } else if (localPath != null && !localPath.isBlank()) {
            log.info("Processing local path: {}", localPath);

            localFileService.processLocalDirectoryForLanguage(
                    localPath,
                    outputName,
                    processor.getIncludePatterns(),
                    processor.getExcludePatterns(),
                    processor.getOutputExtension()
            );

            return new String(Files.readAllBytes(Paths.get(outputDirectory, outputFilename)));
        } else {
            throw new IllegalArgumentException("Either GitHub URL or local path must be provided");
        }
    }

    private String getFilename(String githubUrl, String localPath) {
        if (githubUrl != null && !githubUrl.isBlank()) {
            String[] parts = githubUrl.split("/");
            return parts[parts.length - 1];
        } else if (localPath != null && !localPath.isBlank()) {
            return Paths.get(localPath).getFileName().toString();
        }
        return "output";
    }
}