package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;

public class TaskViewModel {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final StringProperty createdAt = new SimpleStringProperty();
    private final BooleanProperty isCompleted = new SimpleBooleanProperty();
    private final StringProperty deadlineAt = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();

    public TaskViewModel(TaskDTO dto) {
        this.id.set(dto.id());
        this.title.set(dto.title());
        this.content.set(dto.content());
        this.createdAt.set(dto.createdAt());
        this.isCompleted.set(dto.isCompleted());
        this.deadlineAt.set(dto.deadlineAt());
        this.priority.set(dto.priority());
    }

    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty contentProperty() { return content; }
    public StringProperty createdAtProperty() { return createdAt; }
    public BooleanProperty isCompletedProperty() { return isCompleted; }
    public StringProperty deadlineAtProperty() { return deadlineAt; }
    public StringProperty priorityProperty() { return priority; }

    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public boolean isCompleted() { return isCompleted.get(); }
}
