package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.UUID;

public record ScriptId (UUID value) {
    public ScriptId {
        if (value == null) {
            throw ExceptionFactory.validation("ScriptId cannot be null", "ScriptId");
        }
    }

    public static ScriptId random() {
        return new ScriptId(UUID.randomUUID());
    }

    public static ScriptId fromString(String string) {
        if (string == null || string.isBlank()) {
            throw ExceptionFactory.validation("ScriptId string cannot be empty", "ScriptId");
        }
        try {
            UUID uuid = UUID.fromString(string);
            return new ScriptId(uuid);
        } catch (IllegalArgumentException e) {
            throw ExceptionFactory.validation(
                    String.format("Invalid ScriptId string: '%s'", string),
                    "ScriptId"
            );
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
