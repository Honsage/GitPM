package ru.honsage.dev.gitpm.presentation.dto;

public record ScriptDTO(
        String scriptId,
        String title,
        String description,
        String workingDir,
        String command
) {
}
