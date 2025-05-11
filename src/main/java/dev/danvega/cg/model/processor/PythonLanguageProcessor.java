package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class PythonLanguageProcessor implements LanguageProcessor {
    @Override
    public String getLanguageType() {
        return "python";
    }

    @Override
    public List<String> getIncludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getIncludePatterns();

        List<String> pythonPatterns = List.of(
                "**/*.py",
                "**/requirements.txt"
        );

        return Stream.concat(defaultPatterns.stream(), pythonPatterns.stream()).toList();
    }

    @Override
    public List<String> getExcludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getExcludePatterns();

        List<String> pythonExcludePatterns = List.of(
                "**/__pycache__/**",
                "**/venv/**",
                "**/env/**",
                "**/.venv/**",
                "**/.env/**",
                "**/tests/**"
        );

        return Stream.concat(defaultPatterns.stream(), pythonExcludePatterns.stream()).toList();
    }
}
