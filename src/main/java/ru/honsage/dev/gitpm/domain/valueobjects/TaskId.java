package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.UUID;

public record TaskId(UUID value) {
    public TaskId {
        if (value == null) {
            throw ExceptionFactory.validation("TaskId cannot be null", "TaskId");
        }
    }

    public static TaskId random() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId fromString(String string) {
        if (string == null || string.isBlank()) {
            throw ExceptionFactory.validation("TaskId string cannot be empty", "TaskId");
        }
        try {
            UUID uuid = UUID.fromString(string);
            return new TaskId(uuid);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.validation(
                    String.format("Invalid TaskId string: '%s'", string),
                    "TaskId"
            );
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
