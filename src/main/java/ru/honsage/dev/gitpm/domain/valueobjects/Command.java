package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

public record Command(String value) {
    public Command {
        if (value == null || value.isBlank()) {
            throw ExceptionFactory.validation(
                    "Executable command cannot be empty",
                    "Command"
            );
        }
        value = value.trim();
        if (!isCommandValid(value)) {
            throw ExceptionFactory.validation(
                    "Invalid command",
                    "Command"
            );
        }
    }

    private boolean isCommandValid(String command) {
        // TODO: add validation
        return true;
    }

    @Override
    public String toString() {
        return this.value();
    }
}
