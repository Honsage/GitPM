package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record LocalRepositoryPath(String value) {
    public LocalRepositoryPath {
        if (value == null || value.trim().isEmpty()) {
            throw ExceptionFactory.validation(
                    "Local repository path cannot be empty",
                    "LocalRepositoryPath"
            );
        }

        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            throw ExceptionFactory.validation(
                    String.format("Local repository at path: %s does not exist", value),
                    "LocalRepositoryPath"
            );
        }
        if (!Files.isDirectory(path)) {
            throw ExceptionFactory.validation(
                    String.format("Local repository: %s is not a directory", value),
                    "LocalRepositoryPath"
            );
        }

        value = path.toAbsolutePath().normalize().toString();
    }

    public Path toPath() {
        return Paths.get(value);
    }

    public String getRepoName() {
        return toPath().getFileName().toString();
    }
}
