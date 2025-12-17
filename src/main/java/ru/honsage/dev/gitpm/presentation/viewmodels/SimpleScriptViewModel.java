package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.ExecutableCommand;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;
import ru.honsage.dev.gitpm.presentation.dto.SimpleScriptDTO;

public class SimpleScriptViewModel {
    private final StringProperty scriptId = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty commandId = new SimpleStringProperty();
    private final StringProperty workingDir = new SimpleStringProperty();
    private final StringProperty executableCommand = new SimpleStringProperty();

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final BooleanProperty running = new SimpleBooleanProperty(false);

    public SimpleScriptViewModel(SimpleScriptDTO dto) {
        this.scriptId.set(dto.scriptId());
        this.title.set(dto.title());
        this.description.set(dto.description());
        this.commandId.set(dto.commandId());
        this.workingDir.set(dto.workingDir());
        this.executableCommand.set(dto.executableCommand());
    }

    public Command extractCommand() {
        return new Command(
                CommandId.fromString(getCommandId()),
                new WorkingDir(getWorkingDir()),
                ExecutableCommand.parse(getExecutableCommand())
        );
    }

    public StringProperty scriptIdProperty() { return scriptId; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty commandIdProperty() { return commandId; }
    public StringProperty workingDirProperty() { return workingDir; }
    public StringProperty executableCommandProperty() { return executableCommand; }
    public BooleanProperty selectedProperty() { return selected; }
    public BooleanProperty runningProperty() { return running; }

    public String getScriptId() { return scriptId.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getCommandId() { return commandId.get(); }
    public String getWorkingDir() { return workingDir.get(); }
    public String getExecutableCommand() { return executableCommand.get(); }
    public boolean isRunning() { return running.get(); }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean isSelected) { this.selected.set(isSelected); }
    public void setRunning(boolean value) { running.set(value); }
}
