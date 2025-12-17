package ru.honsage.dev.gitpm.presentation.mappers;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;

public class TaskDTOMapper {
    private TaskDTOMapper() {}

    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId().toString(),
                task.getTitle(),
                task.getContent(),
                task.getCreatedAt().toString(),
                task.isCompleted(),
                task.getDeadlineAt() == null ? null : task.getDeadlineAt().toString(),
                task.getPriority().toString()
        );
    }
}
