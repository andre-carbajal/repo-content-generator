package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class KotlinLanguageProcessor implements LanguageProcessor {
    @Override
    public String getLanguageType() {
        return "kotlin";
    }

    @Override
    public List<String> getIncludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getIncludePatterns();

        List<String> kotlinPatterns = List.of(
                "**/*.kt",
                "**/*.kts",
                "**/*.java"
        );

        return Stream.concat(defaultPatterns.stream(), kotlinPatterns.stream()).toList();
    }

    @Override
    public List<String> getExcludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getExcludePatterns();

        List<String> kotlinExcludePatterns = List.of(
                "**/target/**",
                "**/build/**",
                "**/test/**",
                "**/generated/**",
                "gradle/**",
                "gradlew",
                "gradlew.bat"
        );

        return Stream.concat(defaultPatterns.stream(), kotlinExcludePatterns.stream()).toList();
    }
}
