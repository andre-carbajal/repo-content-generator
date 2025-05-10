package dev.danvega.cg.processor;

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
        return List.of("**/*.py");
    }

    @Override
    public List<String> getExcludePatterns() {
        // Common Python files/directories to exclude
        return List.of(
                "**/__pycache__/**",
                "**/.venv/**",
                "**/test/**",
                "**/tests/**"
        );
    }
}
