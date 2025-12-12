package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.valueobjects.TaskId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final TaskId id;
    private String title;
    private String content;
    private final LocalDateTime createdAt;
    private boolean isCompleted;
    private LocalDateTime deadlineAt;
    private TaskPriority priority;

    public Task(
            TaskId id,
            String title,
            String content,
            LocalDateTime createdAt,
            boolean isCompleted,
            LocalDateTime deadlineAt,
            TaskPriority priority
    ) {
        this.id = Objects.requireNonNull(id, "Task id must be notNull");
        validateTitle(title);
        validateCreatedAt(createdAt);
        validateDeadline(deadlineAt);
        validatePriority(priority);
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isCompleted = isCompleted;
        this.deadlineAt = deadlineAt;
        this.priority = priority;
    }

    public Task(TaskId id, String title) {
        this(
                id,
                title,
                null,
                LocalDateTime.now(),
                false,
                null,
                TaskPriority.LOW
        );
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw ExceptionFactory.validation("Title of Task cannot be empty", "Task.title");
        }
    }

    private void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw ExceptionFactory.validation("Creation datetime cannot be null", "Task.createdAt");
        }

        if (createdAt.isAfter(LocalDateTime.now())) {
            throw ExceptionFactory.validation(
                    "Creation datetime cannot be in the future",
                    "Task.createdAt"
            );
        }
    }

    private void validateDeadline(LocalDateTime deadlineAt) {
        if (deadlineAt != null && deadlineAt.isBefore(createdAt)) {
            throw ExceptionFactory.validation("Deadline cannot be before creation", "Task.deadline");
        }
    }

    private void validatePriority(TaskPriority priority) {
        if (priority == null) {
            throw ExceptionFactory.validation("Priority cannot be empty", "Task.priority");
        }
    }

    public void complete() {
        this.isCompleted = true;
    }

    public void uncomplete() {
        this.isCompleted = false;
    }

    public void changePriority(TaskPriority newPriority) {
        validatePriority(newPriority);
        this.priority = newPriority;
    }

    public void update(
            String newTitle,
            String newContent,
            boolean isCompleted,
            LocalDateTime newDeadlineAt,
            TaskPriority newPriority
    ) {
        validateTitle(newTitle);
        validateDeadline(newDeadlineAt);
        this.title = newTitle;
        this.content = newContent;
        this.deadlineAt = newDeadlineAt;
        if (isCompleted) complete();
        else uncomplete();
        changePriority(newPriority);
    }

    public boolean isOverdue() {
        return this.deadlineAt != null
                && !isCompleted
                && this.deadlineAt.isBefore(LocalDateTime.now());
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public LocalDateTime getDeadlineAt() {
        return this.deadlineAt;
    }

    public TaskPriority getPriority() {
        return this.priority;
    }
}
