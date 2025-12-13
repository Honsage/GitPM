package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.beans.property.*;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;

public class TaskViewModel {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final StringProperty createdAt = new SimpleStringProperty();
    private final BooleanProperty isCompleted = new SimpleBooleanProperty();
    private final StringProperty deadlineAt = new SimpleStringProperty();
    private final ObjectProperty<TaskPriority> priority = new SimpleObjectProperty<>(TaskPriority.LOW);

    public TaskViewModel(TaskDTO dto) {
        this.id.set(dto.id());
        this.title.set(dto.title());
        this.content.set(dto.content());
        this.createdAt.set(dto.createdAt());
        this.isCompleted.set(dto.isCompleted());
        this.deadlineAt.set(dto.deadlineAt());
        this.priority.set(TaskPriority.valueOf(dto.priority()));
    }

    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty contentProperty() { return content; }
    public StringProperty createdAtProperty() { return createdAt; }
    public BooleanProperty completedProperty() { return isCompleted; }
    public StringProperty deadlineAtProperty() { return deadlineAt; }
    public ObjectProperty<TaskPriority> priorityProperty() { return priority; }

    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public boolean isCompleted() { return isCompleted.get(); }
    public TaskPriority getPriority() { return priority.get(); }

    public void setPriority(TaskPriority priority) {
        this.priority.set(priority);
    }
}
