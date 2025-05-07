package dev.danvega.cg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Paths;

@Controller
public class ContentGeneratorController {
    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorController.class);
    private final ContentGeneratorService contentGeneratorService;

    public ContentGeneratorController(ContentGeneratorService contentGeneratorService) {
        this.contentGeneratorService = contentGeneratorService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> downloadRepository(@RequestParam(required = false) String url,
                                                     @RequestParam(required = false) String localPath) {
        try {
            String content = contentGeneratorService.generateContent(url, localPath);
            String filename = getFilename(url, localPath);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + filename + ".md")
                    .header("Content-Type", "text/markdown")
                    .body(content.getBytes());
        } catch (Exception e) {
            log.error("Error generating content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

