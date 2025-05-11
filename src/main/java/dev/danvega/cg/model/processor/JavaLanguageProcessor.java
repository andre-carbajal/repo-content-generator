package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class JavaLanguageProcessor implements LanguageProcessor {

    @Override
    public String getLanguageType() {
        return "java";
    }

    @Override
    public List<String> getIncludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getIncludePatterns();

        List<String> javaPatterns = List.of(
                "**/*.java",
                "**/*.xml",
                "**/*.gradle",
                "pom.xml"
        );

        return Stream.concat(defaultPatterns.stream(), javaPatterns.stream()).toList();
    }

    @Override
    public List<String> getExcludePatterns() {
        List<String> defaultPatterns = LanguageProcessor.super.getExcludePatterns();

        List<String> javaExcludePatterns = List.of(
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

        return Stream.concat(defaultPatterns.stream(), javaExcludePatterns.stream()).toList();
    }
}