package dev.danvega.cg.model.processor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLanguageProcessor implements LanguageProcessor {

    /**
     * Returns the base list of file patterns common to all languages to include in processing.
     * This includes common configuration and documentation files.
     *
     * @return list of common file patterns to include
     */
    protected List<String> getBaseIncludePatterns() {
        return List.of(
                "**/*.md",
                "**/*.txt",
                "**/*.yml",
                "**/*.yaml",
                "**/*.properties",
                "Dockerfile"
        );
    }

    /**
     * Returns the base list of file patterns common to all languages to exclude from processing.
     * This includes common system and IDE-specific files.
     *
     * @return list of common file patterns to exclude
     */
    protected List<String> getBaseExcludePatterns() {
        return List.of(
                ".github/**",
                ".gitattributes",
                ".gitignore",
                ".idea/**",
                ".DS_Store",
                ".vscode/**"
        );
    }

    /**
     * Returns language-specific patterns to include.
     * Subclasses should override this method to provide language-specific patterns.
     *
     * @return list of language-specific include patterns
     */
    protected abstract List<String> getLanguageSpecificIncludePatterns();

    /**
     * Returns language-specific patterns to exclude.
     * Subclasses should override this method to provide language-specific patterns.
     *
     * @return list of language-specific exclude patterns
     */
    protected abstract List<String> getLanguageSpecificExcludePatterns();

    @Override
    public List<String> getIncludePatterns() {
        List<String> allPatterns = new ArrayList<>(getBaseIncludePatterns());
        allPatterns.addAll(getLanguageSpecificIncludePatterns());
        allPatterns.add(getLanguageExtensionPattern());
        return allPatterns;
    }

    @Override
    public List<String> getExcludePatterns() {
        List<String> allPatterns = new ArrayList<>(getBaseExcludePatterns());
        allPatterns.addAll(getLanguageSpecificExcludePatterns());
        return allPatterns;
    }

    @Override
    public String getOutputExtension() {
        return "txt";
    }
}
