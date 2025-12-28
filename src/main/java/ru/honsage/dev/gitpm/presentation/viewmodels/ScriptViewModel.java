package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.honsage.dev.gitpm.domain.valueobjects.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;
import ru.honsage.dev.gitpm.presentation.dto.ScriptDTO;

public class ScriptViewModel {
    private final StringProperty scriptId = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty workingDir = new SimpleStringProperty();
    private final StringProperty command = new SimpleStringProperty();

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);

    public ScriptViewModel(ScriptDTO dto) {
        this.scriptId.set(dto.scriptId());
        this.title.set(dto.title());
        this.description.set(dto.description());
        this.workingDir.set(dto.workingDir());
        this.command.set(dto.command());
    }

    public StringProperty scriptIdProperty() { return scriptId; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty workingDirProperty() { return workingDir; }
    public StringProperty commandProperty() { return command; }
    public BooleanProperty selectedProperty() { return selected; }
    public BooleanProperty runningProperty() { return running; }

    public String getScriptId() { return scriptId.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getWorkingDir() { return workingDir.get(); }
    public String getCommand() { return command.get(); }
    public boolean isRunning() { return running.get(); }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean isSelected) { this.selected.set(isSelected); }
    public void setRunning(boolean value) { running.set(value); }
}
