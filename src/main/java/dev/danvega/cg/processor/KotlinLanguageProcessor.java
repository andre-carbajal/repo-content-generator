package dev.danvega.cg.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KotlinLanguageProcessor implements LanguageProcessor {

    @Override
    public String getLanguageType() {
        return "kotlin";
    }

    @Override
    public List<String> getIncludePatterns() {
        return List.of("**/*.kt", "**/*.kts");
    }

    @Override
    public List<String> getExcludePatterns() {
        return List.of(
                "**/build/**",
                "**/test/**",
                "**/generated/**"
        );
    }
}
