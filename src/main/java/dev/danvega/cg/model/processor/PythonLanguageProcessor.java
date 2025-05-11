package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PythonLanguageProcessor implements LanguageProcessor {
    @Override
    public String getLanguageType() {
        return "python";
    }

    @Override
    public List<String> getIncludePatterns() {
        return combinePatterns(
                List.of(
                        "**/*.py"
                ),
                LanguageProcessor.super.getIncludePatterns()
        );
    }

    @Override
    public List<String> getExcludePatterns() {
        return combinePatterns(
                List.of(
                        "**/__pycache__/**",
                        "**/.venv/**",
                        "**/test/**",
                        "**/tests/**"
                ),
                LanguageProcessor.super.getExcludePatterns()
        );
    }
}
