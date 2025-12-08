package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Project;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Optional<Project> findById(Long id);
    Optional<Project> findByLocalPath(Path directory);
    List<Project> findAll();
    List<Project> findByTitlePrefix(String titlePrefix);
    List<Project> findWithRemote();

    Project save(Project project);
    Project update(Long id, Project project);
    void delete(Long id);
}
