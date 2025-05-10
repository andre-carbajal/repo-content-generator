package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JavaLanguageProcessor implements LanguageProcessor {

    @Override
    public String getLanguageType() {
        return "java";
    }

    @Override
    public List<String> getIncludePatterns() {
        return List.of("**/*.java");
    }

    @Override
    public List<String> getExcludePatterns() {
        return List.of(
                "**/target/**",
                "**/build/**",
                "**/test/**",
                "**/generated/**"
        );
    }
}
