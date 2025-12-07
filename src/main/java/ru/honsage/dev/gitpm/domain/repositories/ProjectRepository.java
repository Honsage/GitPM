package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Optional<Project> findById(Long id);
    Optional<Project> findByTitle(String title);
    List<Project> findAll();
    List<Project> findWithRemote();

    Project save(Project project);
    void delete(Long id);
}
