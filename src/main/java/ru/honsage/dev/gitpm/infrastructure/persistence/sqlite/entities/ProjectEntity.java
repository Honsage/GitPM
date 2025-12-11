package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities;

public record ProjectEntity(
        String id,
        String title,
        String description,
        String localPath,
        String remoteURL,
        String addedAt
) {
}
