package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record WorkingDir(String value) {
    public WorkingDir {
        if (value == null || value.trim().isEmpty()) {
            throw ExceptionFactory.validation(
                    "Working directory cannot be empty",
                    "WorkingDir"
            );
        }

        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            throw ExceptionFactory.validation(
                    String.format("Working directory at path: %s does not exist", value),
                    "WorkingDir"
            );
        }
        if (!Files.isDirectory(path)) {
            throw ExceptionFactory.validation(
                    String.format("Working directory: %s is not a directory", value),
                    "WorkingDir"
            );
        }

        value = path.toAbsolutePath().normalize().toString();
    }

    public Path toPath() {
        return Paths.get(value);
    }
}
