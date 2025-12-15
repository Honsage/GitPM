package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.UUID;

public record CommandId(UUID value) {
    public CommandId {
        if (value == null) {
            throw ExceptionFactory.validation("CommandId cannot be null", "CommandId");
        }
    }

    public static CommandId random() {
        return new CommandId(UUID.randomUUID());
    }

    public static CommandId fromString(String string) {
        if (string == null || string.isBlank()) {
            throw ExceptionFactory.validation("CommandId string cannot be empty", "CommandId");
        }
        try {
            UUID uuid = UUID.fromString(string);
            return new CommandId(uuid);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.validation(
                    String.format("Invalid CommandId string: '%s'", string),
                    "CommandId"
            );
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
