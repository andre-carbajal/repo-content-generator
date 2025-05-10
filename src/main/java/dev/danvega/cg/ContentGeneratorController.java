package dev.danvega.cg;

import dev.danvega.cg.processor.LanguageProcessor;
import dev.danvega.cg.processor.LanguageProcessorRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class ContentGeneratorController {
    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorController.class);
    private final ContentGeneratorService contentGeneratorService;
    private final LanguageProcessorRegistry processorRegistry;

    @GetMapping("/generate")
    public ResponseEntity<?> downloadContent(
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String localPath,
            @RequestParam(required = false, defaultValue = "java") String type) {

        try {
            if (processorRegistry.getProcessor(type).isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(("Unsupported language type: " + type +
                                ". Supported types: " + String.join(", ", processorRegistry.getSupportedLanguages()))
                                .getBytes());
            }

            String content = contentGeneratorService.generateContent(url, localPath, type);
            String filename = getFilename(url, localPath);
            String outputExtension = processorRegistry.getProcessor(type)
                    .map(LanguageProcessor::getOutputExtension)
                    .orElse("txt");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename + "." + outputExtension)
                    .header("Content-Type", "text/plain")
                    .body(content.getBytes());
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.internalServerError().body("Error generating content: " + e.getMessage());
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
