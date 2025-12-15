package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.UUID;

public record ScriptCommandId(UUID value) {
    public ScriptCommandId {
        if (value == null) {
            throw ExceptionFactory.validation("CommandId cannot be null", "CommandId");
        }
    }

    public static ScriptCommandId random() {
        return new ScriptCommandId(UUID.randomUUID());
    }

    public static ScriptCommandId fromString(String string) {
        if (string == null || string.isBlank()) {
            throw ExceptionFactory.validation("CommandId string cannot be empty", "CommandId");
        }
        try {
            UUID uuid = UUID.fromString(string);
            return new ScriptCommandId(uuid);
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
