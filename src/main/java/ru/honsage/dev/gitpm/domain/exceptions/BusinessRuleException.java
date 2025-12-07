package ru.honsage.dev.gitpm.domain.exceptions;

public class BusinessRuleException extends RuntimeException {
    protected BusinessRuleException(String message) {
        super(message);
    }
}
