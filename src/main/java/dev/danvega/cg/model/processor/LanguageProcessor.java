package dev.danvega.cg.model.processor;

import java.util.List;

/**
 * Interface defining language-specific processing capabilities for code content generation.
 */
public interface LanguageProcessor {

    /**
     * Returns the identifier for the programming language this processor handles.
     *
     * @return the language type identifier as a string
     */
    String getLanguageType();

    /**
     * Returns the list of file patterns to include in processing.
     *
     * @return list of file patterns to include
     */
    List<String> getIncludePatterns();

    /**
     * Returns the list of file patterns to exclude from processing.
     *
     * @return list of file patterns to exclude
     */
    List<String> getExcludePatterns();

    /**
     * Returns the file extension for the output file.
     *
     * @return the output file extension
     */
    String getOutputExtension();
}