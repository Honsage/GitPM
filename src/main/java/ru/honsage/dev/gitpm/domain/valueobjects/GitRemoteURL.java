package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

public record GitRemoteURL(String value) {
    public GitRemoteURL {
        if (value != null) {
            value = value.trim();

            if (value.isEmpty()) {
                throw ExceptionFactory.validation(
                        "Git remote URL cannot be empty",
                        "GitRemoteURL"
                );
            }
            if (!isGitURLValid(value)) {
                throw ExceptionFactory.validation(
                        String.format("Invalid git remote URL: %s", value),
                        "GitRemoteURL"
                );
            }
        }
    }

    private boolean isGitURLValid(String url) {
        return url.matches("(https://)?(github\\.com|gitlab\\.com|bitbucket\\.org|gitverse\\.ru)/" +
                "[A-Za-z0-9_\\-.]+/[A-Za-z0-9_\\-.]+");
    }
}
