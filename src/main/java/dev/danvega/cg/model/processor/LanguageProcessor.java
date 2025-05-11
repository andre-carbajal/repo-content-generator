package dev.danvega.cg.model.processor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Interface defining language-specific processing capabilities for code content generation.
 * Provides default patterns for file inclusion/exclusion and methods to combine patterns.
 */
public interface LanguageProcessor {
    /**
     * Returns the identifier for the programming language this processor handles.
     *
     * @return the language type identifier as a string
     */
    String getLanguageType();

    /**
     * Returns the default list of file patterns to include in processing.
     * This includes common configuration and documentation files.
     *
     * @return list of file patterns to include
     */
    default List<String> getIncludePatterns() {
        return List.of(
                "**/*.md",
                "**/*.txt",
                "**/*.yml",
                "**/*.yaml",
                "**/*.properties",
                "**/Dockerfile"
        );
    }

    /**
     * Returns the default list of file patterns to exclude from processing.
     * This includes common system and IDE-specific files.
     *
     * @return list of file patterns to exclude
     */
    default List<String> getExcludePatterns() {
        return List.of(
                "**/.github/**",
                "**/.gitattributes",
                "**/.gitignore",
                ".idea/**",
                ".DS_Store/**",
                ".vscode/**"
        );
    }

    /**
     * Combines two lists of file patterns into a single list.
     * The resulting list contains all patterns from the base list followed by all patterns from the additional list.
     *
     * @param additionalPatterns the list of additional patterns to include
     * @param basePatterns the base list of patterns
     * @return a combined list containing all patterns from both input lists
     */
    default List<String> combinePatterns(List<String> additionalPatterns, List<String> basePatterns) {
        return Stream.concat(basePatterns.stream(), additionalPatterns.stream()).toList();
    }

    /**
     * Returns the file extension for the output file.
     * Defaults to "txt" if not overridden.
     *
     * @return the output file extension
     */
    default String getOutputExtension() {
        return "txt";
    }
}