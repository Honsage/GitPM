package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.repositories.TaskRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.TaskId;

import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(
            ProjectId projectId,
            String title,
            String content,
            LocalDateTime deadlineAt,
            TaskPriority priority
    ) {
        Task task = new Task(
                TaskId.random(),
                title,
                content,
                LocalDateTime.now(),
                false,
                deadlineAt,
                priority
        );
        return taskRepository.save(task, projectId);
    }

    public Task getTask(ProjectId projectId, TaskId taskId) {
        return taskRepository.findById(taskId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Task with id '%s' is not found", taskId)
                ));
    }

    public List<Task> getAllTasks(ProjectId projectId) {
        return taskRepository.findAllByProject(projectId);
    }

    public List<Task> getTasksByTitlePrefix(ProjectId projectId, String titlePrefix) {
        return taskRepository.findByTitlePrefix(titlePrefix, projectId);
    }

    public List<Task> getTasksByCompleted(ProjectId projectId, boolean isCompleted) {
        return taskRepository.findByCompleted(isCompleted, projectId);
    }

    public List<Task> getTasksByPriority(ProjectId projectId, TaskPriority priority) {
        return taskRepository.findByPriority(priority, projectId);
    }

    public List<Task> getOverdueTasks(ProjectId projectId) {
        return taskRepository.findOverdue(projectId);
    }

    public Task updateTask(
            ProjectId projectId,
            TaskId taskId,
            String newTitle,
            String newContent,
            boolean isCompleted,
            LocalDateTime newDeadlineAt,
            TaskPriority newPriority
    ) {
        Task task = getTask(projectId, taskId);

        task.update(
                newTitle,
                newContent,
                isCompleted,
                newDeadlineAt,
                newPriority
        );

        return taskRepository.update(task, projectId);
    }

    public void deleteTask(ProjectId projectId, TaskId taskId) {
        taskRepository.delete(taskId, projectId);
    }
}
