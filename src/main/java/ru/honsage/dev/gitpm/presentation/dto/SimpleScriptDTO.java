package ru.honsage.dev.gitpm.presentation.dto;

public record SimpleScriptDTO(
        String scriptId,
        String title,
        String description,
        String commandId,
        String workingDir,
        String executableCommand
) {
}
