package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class PythonLanguageProcessor extends AbstractLanguageProcessor {

    @Override
    public String getLanguageType() {
        return "python";
    }

    @Override
    protected List<String> getLanguageSpecificIncludePatterns() {
        return List.of(
                "**/*.py",
                "**/requirements.txt"
        );
    }

    @Override
    protected List<String> getLanguageSpecificExcludePatterns() {
        return List.of(
                "**/__pycache__/**",
                "**/venv/**",
                "**/env/**",
                "**/.venv/**",
                "**/.env/**",
                "**/tests/**"
        );
    }
}