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
        return combineIncludePatterns(List.of(
                "**/*.py"
        ));
    }

    @Override
    public List<String> getExcludePatterns() {
        return combineExcludePatterns(
                List.of(
                        "**/__pycache__/**",
                        "**/.venv/**",
                        "**/test/**",
                        "**/tests/**"
                )
        );
    }
}
