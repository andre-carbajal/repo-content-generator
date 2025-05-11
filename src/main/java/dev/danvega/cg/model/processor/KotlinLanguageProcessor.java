package dev.danvega.cg.model.processor;

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
        return combinePatterns(
                List.of(
                        "**/*.kt",
                        "**/*.kts",
                        "**/*.java"
                ),
                LanguageProcessor.super.getIncludePatterns()
        );
    }

    @Override
    public List<String> getExcludePatterns() {
        return combinePatterns(
                List.of(
                        "**/target/**",
                        "**/build/**",
                        "**/test/**",
                        "**/generated/**",
                        "gradle/**",
                        "gradlew",
                        "gradlew.bat"
                ),
                LanguageProcessor.super.getExcludePatterns()
        );
    }
}
