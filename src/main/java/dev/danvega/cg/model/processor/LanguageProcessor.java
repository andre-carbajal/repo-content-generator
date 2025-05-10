package dev.danvega.cg.model.processor;

import java.util.List;

/**
 * Interface for language-specific file processors.
 * Each implementation defines which files to include or exclude for a specific language.
 */
public interface LanguageProcessor {
    /**
     * Get the language identifier (e.g., "java", "kotlin", "python")
     */
    String getLanguageType();

    /**
     * Get patterns for files that should be included
     */
    List<String> getIncludePatterns();

    /**
     * Get patterns for files that should be excluded
     */
    List<String> getExcludePatterns();

    /**
     * Get the file extension for the output file
     */
    default String getOutputExtension() {
        return "txt";
    }
}