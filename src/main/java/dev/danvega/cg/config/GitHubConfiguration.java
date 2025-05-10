package dev.danvega.cg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "github")
public record GitHubConfiguration(String token) {
    public GitHubConfiguration {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("GitHub token must not be null or blank");
        }
    }
}
