package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities;

public record TaskEntity(
    String id,
    String projectId,
    String title,
    String content,
    String createdAt,
    int isCompleted,
    String deadlineAt,
    String priority
) {
}
