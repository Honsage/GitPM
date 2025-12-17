package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.TaskId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.TaskEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TaskEntityMapper {
    public static TaskEntity toEntity(Task task, ProjectId id) {
        return new TaskEntity(
                task.getId().toString(),
                id.toString(),
                task.getTitle(),
                task.getContent(),
                task.getCreatedAt().toString(),
                task.isCompleted() ? 1 : 0,
                task.getDeadlineAt() == null ? null : task.getDeadlineAt().toString(),
                task.getPriority().toString()
        );
    }

    public static Task toDomain(TaskEntity entity) {
        return new Task(
                TaskId.fromString(entity.id()),
                entity.title(),
                entity.content(),
                LocalDateTime.parse(entity.createdAt()),
                entity.isCompleted() == 1,
                entity.deadlineAt() == null ? null : LocalDateTime.parse(entity.deadlineAt()),
                TaskPriority.valueOf(entity.priority())
        );
    }

    public static TaskEntity fromResultSet(ResultSet rs) throws SQLException {
        return new TaskEntity(
                rs.getString("id_task"),
                rs.getString("id_project"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("created_at"),
                rs.getInt("is_completed"),
                rs.getString("deadline_at"),
                rs.getString("priority")
        );
    }
}
