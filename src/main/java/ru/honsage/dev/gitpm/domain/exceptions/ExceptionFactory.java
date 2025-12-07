package ru.honsage.dev.gitpm.domain.exceptions;

public final class ExceptionFactory {
    public static ValidationException validation(String message, String context) {
        return new ValidationException(message, context);
    }

    public static BusinessRuleException businessRule(String message) {
        return new BusinessRuleException(message);
    }

    public static EntityNotFoundException entityNotFound(String entityName, String key) {
        return new EntityNotFoundException(entityName, key);
    }
}
