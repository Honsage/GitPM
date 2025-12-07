package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findById(Long id);
    Optional<Task> findByTitle(String title);
    List<Task> findAll();
    List<Task> findByCompleted(boolean isCompleted);
    List<Task> findByPriority(TaskPriority priority);
    List<Task> findOverdue();

    Task save(Task task);
    void delete(Long id);
}
