package ru.honsage.dev.gitpm.domain.exceptions;

public class ValidationException extends RuntimeException {
    private final String context;

    protected ValidationException(String message, String context) {
        super(message);
        this.context = context;
    }

    public String getContext() {
        return this.context;
    }
}
