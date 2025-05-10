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
        return combineIncludePatterns(List.of(
                "**/*.java",
                "**/*.xml",
                "**/*.gradle",
                "**/*.pom"
        ));
    }

    @Override
    public List<String> getExcludePatterns() {
        return combineExcludePatterns(List.of(
                "**/target/**",
                "**/build/**",
                "**/test/**",
                "**/generated/**",
                ".mvn/**",
                "mvnw",
                "mvnw.cmd",
                "gradle/**",
                "gradlew",
                "gradlew.bat"
        ));
    }
}
