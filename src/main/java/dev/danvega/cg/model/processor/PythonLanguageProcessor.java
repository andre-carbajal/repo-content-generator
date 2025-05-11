package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PythonLanguageProcessor extends AbstractLanguageProcessor {

    @Override
    public String getLanguageType() {
        return "python";
    }

    @Override
    public String getLanguageExtensionPattern() {
        return "**/*.py";
    }

    @Override
    protected List<String> getLanguageSpecificIncludePatterns() {
        return List.of(
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