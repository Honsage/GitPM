package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Optional<Project> findById(ProjectId id);
    Optional<Project> findByLocalPath(LocalRepositoryPath localPath);
    List<Project> findAll();
    List<Project> findByTitlePrefix(String titlePrefix);
    List<Project> findWithRemote();

    Project save(Project project);
    Project update(Project project);
    void delete(ProjectId id);
}
