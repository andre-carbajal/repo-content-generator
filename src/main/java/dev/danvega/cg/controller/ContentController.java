package dev.danvega.cg.controller;

import dev.danvega.cg.model.processor.LanguageProcessor;
import dev.danvega.cg.model.processor.LanguageProcessorRegistry;
import dev.danvega.cg.service.ContentGeneratorService;
import dev.danvega.cg.util.PathUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {
    private static final Logger log = LoggerFactory.getLogger(ContentController.class);
    private final ContentGeneratorService contentGeneratorService;
    private final PathUtils pathUtils;
    private final LanguageProcessorRegistry processorRegistry;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> downloadContent(
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String localPath,
            @RequestParam(required = false, defaultValue = "java") String type,
            @RequestParam(required = false, defaultValue = "false") boolean onlyLanguageFiles) {

        try {
            if (processorRegistry.getProcessor(type).isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(("Unsupported language type: " + type +
                                ". Supported types: " + String.join(", ", processorRegistry.getSupportedLanguages()))
                                .getBytes());
            }

            String content = contentGeneratorService.generateContent(url, localPath, type, onlyLanguageFiles);            String filename = pathUtils.determineFilename(url, localPath);
            String outputExtension = processorRegistry.getProcessor(type)
                    .map(LanguageProcessor::getOutputExtension)
                    .orElse("txt");

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename + "." + outputExtension)
                    .header("Content-Type", "text/plain")
                    .body(content.getBytes());
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
