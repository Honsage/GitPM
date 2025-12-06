package ru.honsage.dev.gitpm.domain.exceptions;

public final class ExceptionFactory {
    public static ValidationException validation(String message, String context) {
        return new ValidationException(message, context);
    }
}
