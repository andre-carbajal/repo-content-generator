package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KotlinLanguageProcessor extends AbstractLanguageProcessor {

    @Override
    public String getLanguageType() {
        return "kotlin";
    }

    @Override
    public String getLanguageExtensionPattern() {
        return "**/*.kt";
    }

    @Override
    protected List<String> getLanguageSpecificIncludePatterns() {
        return List.of(
                "**/*.kts",
                "**/*.java"
        );
    }

    @Override
    protected List<String> getLanguageSpecificExcludePatterns() {
        return List.of(
                "**/target/**",
                "**/build/**",
                "**/test/**",
                "**/generated/**",
                "gradle/**",
                "gradlew",
                "gradlew.bat"
        );
    }
}