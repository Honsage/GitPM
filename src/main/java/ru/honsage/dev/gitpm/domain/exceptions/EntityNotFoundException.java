package ru.honsage.dev.gitpm.domain.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private final String key;

    protected EntityNotFoundException(String entityName, String key) {
        super(entityName);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
