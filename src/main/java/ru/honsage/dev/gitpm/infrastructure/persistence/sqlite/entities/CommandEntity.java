package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities;

public record CommandEntity(
        String id,
        String scriptId,
        String workingDir,
        String executableCommand,
        int order
) {
}
