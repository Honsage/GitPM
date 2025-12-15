package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.TaskId;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findById(TaskId taskId);
    List<Task> findByTitlePrefix(String titlePrefix, ProjectId id);
    List<Task> findAllByProject(ProjectId id);
    List<Task> findByCompleted(boolean isCompleted, ProjectId id);
    List<Task> findByPriority(TaskPriority priority, ProjectId id);
    List<Task> findOverdue(ProjectId id);

    Task save(Task task, ProjectId id);
    Task update(Task task, ProjectId id);
    void delete(TaskId taskId);
}
