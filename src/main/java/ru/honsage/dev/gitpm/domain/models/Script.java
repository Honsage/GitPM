package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;

import java.util.Objects;

public class Script {
    private final ScriptId id;
    private String title;
    private String description;

    public Script(
            ScriptId id,
            String title,
            String description
    ) {
        this.id = Objects.requireNonNull(id);
        validateTitle(title);
        this.title = title;
        this.description = description;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw ExceptionFactory.validation("Title of Script cannot be empty", "Script.title");
        }
    }
}
