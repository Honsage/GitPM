package ru.honsage.dev.gitpm.presentation.dto;

public record TaskDTO(
        String id,
        String title,
        String content,
        String createdAt,
        boolean isCompleted,
        String deadlineAt,
        String priority
) {
}
