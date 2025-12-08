package ru.honsage.dev.gitpm.presentation.dto;

public record ProjectDTO(
        String id,
        String title,
        String description,
        String localPath,
        String remoteURL,
        String addedAt
) {}
