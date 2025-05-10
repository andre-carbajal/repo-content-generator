package dev.danvega.cg.util;

import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for path operations and pattern matching.
 */
@Component
public class PathUtils {

    /**
     * Determines the filename based on the provided GitHub repository URL or local path.
     *
     * @param githubRepoUrl The GitHub repository URL
     * @param localPath The local path
     * @return The determined filename
     */
    public String determineFilename(String githubRepoUrl, String localPath) {
        if (githubRepoUrl != null && !githubRepoUrl.isBlank()) {
            String[] parts = githubRepoUrl.split("/");
            return parts[parts.length - 1];
        } else if (localPath != null && !localPath.isBlank()) {
            return Paths.get(localPath).getFileName().toString();
        }
        return "output";
    }

    /**
     * Checks if a path is excluded based on exclude patterns.
     *
     * @param path The path to check
     * @param excludePatterns List of exclude patterns
     * @return true if the path matches any exclude pattern, false otherwise
     */
    public boolean isExcluded(String path, List<String> excludePatterns) {
        if (excludePatterns == null || excludePatterns.isEmpty()) {
            return false;
        }
        return matchesAnyPattern(path, excludePatterns);
    }

    /**
     * Determines if a file should be included based on include and exclude patterns.
     *
     * @param filePath The file path to check
     * @param includePatterns List of include patterns
     * @param excludePatterns List of exclude patterns
     * @return true if the file should be included, false otherwise
     */
    public boolean shouldIncludeFile(String filePath, List<String> includePatterns, List<String> excludePatterns) {
        if (isExcluded(filePath, excludePatterns)) {
            return false;
        }

        return includePatterns == null || includePatterns.isEmpty() || matchesAnyPattern(filePath, includePatterns);
    }

    public boolean shouldIncludeFile(Path filePath, Path sourceDir, List<PathMatcher> includeMatchers, List<PathMatcher> excludeMatchers) {
        String relativePath = normalizePath(sourceDir.relativize(filePath));
        Path normalizedPath = Paths.get(relativePath);

        for (PathMatcher matcher : excludeMatchers) {
            if (matcher.matches(normalizedPath)) {
                return false;
            }
        }

        if (includeMatchers.isEmpty()) {
            return true;
        }

        for (PathMatcher matcher : includeMatchers) {
            if (matcher.matches(normalizedPath)) {
                return true;
            }
        }

        return false;
    }

    public boolean isExcludedDirectory(String dirPath, List<String> excludePatterns) {
        return matchesAnyPattern(dirPath, excludePatterns);
    }

    /**
     * Checks if a path matches any pattern in the given list.
     *
     * @param path The path to check
     * @param patterns List of patterns to match against
     * @return true if the path matches any pattern, false otherwise
     */
    private boolean matchesAnyPattern(String path, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }

        path = normalizePath(path);

        for (String pattern : patterns) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern));
            if (matcher.matches(Paths.get(path))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a list of PathMatchers from a list of pattern strings.
     *
     * @param patterns List of pattern strings
     * @return List of PathMatchers
     */
    public List<PathMatcher> createPathMatchers(List<String> patterns) {
        return patterns.stream().map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + normalizePattern(pattern))).collect(Collectors.toList());
    }

    /**
     * Normalizes a pattern by trimming and replacing backslashes with forward slashes.
     *
     * @param pattern The pattern to normalize
     * @return The normalized pattern
     */
    private String normalizePattern(String pattern) {
        return pattern.trim().replace('\\', '/');
    }

    /**
     * Normalizes a path by replacing backslashes with forward slashes.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    private String normalizePath(String path) {
        return path.replace('\\', '/');
    }

    /**
     * Normalizes a path by replacing backslashes with forward slashes.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    private String normalizePath(Path path) {
        return path.toString().replace('\\', '/');
    }
}
