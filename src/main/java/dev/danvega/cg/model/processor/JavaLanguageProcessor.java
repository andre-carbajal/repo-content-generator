package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JavaLanguageProcessor extends AbstractLanguageProcessor {

    @Override
    public String getLanguageType() {
        return "java";
    }

    @Override
    protected List<String> getLanguageSpecificIncludePatterns() {
        return List.of(
                "**/*.java",
                "**/*.xml",
                "**/*.gradle",
                "pom.xml"
        );
    }

    @Override
    protected List<String> getLanguageSpecificExcludePatterns() {
        return List.of(
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
        );
    }
}