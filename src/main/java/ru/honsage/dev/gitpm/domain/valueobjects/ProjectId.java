package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.UUID;

public record ProjectId(UUID value) {
    public ProjectId {
        if (value == null) {
            throw ExceptionFactory.validation("ProjectId cannot be null", "ProjectId");
        }
    }

    public static ProjectId random() {
        return new ProjectId(UUID.randomUUID());
    }

    public static ProjectId fromString(String string) {
        if (string == null || string.isBlank()) {
            throw ExceptionFactory.validation("ProjectId string cannot be empty", "ProjectId");
        }
        try {
            UUID uuid = UUID.fromString(string);
            return new ProjectId(uuid);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.validation(
                    String.format("Invalid ProjectId string: '%s'", string),
                    "ProjectId"
            );
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
