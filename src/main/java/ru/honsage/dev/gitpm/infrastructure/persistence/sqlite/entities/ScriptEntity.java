package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities;

public record ScriptEntity(
        String id,
        String projectId,
        String title,
        String description,
        String workingDir,
        String command
) {
}
