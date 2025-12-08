package ru.honsage.dev.gitpm.presentation.mappers;

import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;

public class ProjectMapper {
    private ProjectMapper() {}

    public static ProjectDTO toDTO(Project project) {
        return new ProjectDTO(
                project.getId().toString(),
                project.getTitle(),
                project.getDescription(),
                project.getLocalPath().value(),
                project.getRemoteURL() == null ? null : project.getRemoteURL().value(),
                project.getAddedAt().toString()
        );
    }
}
