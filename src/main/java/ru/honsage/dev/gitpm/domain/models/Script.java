package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.valueobjects.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.Objects;

public class Script {
    private final ScriptId id;
    private String title;
    private String description;
    private WorkingDir workingDir;
    private Command command;

    public Script(
            ScriptId id,
            String title,
            String description,
            WorkingDir workingDir,
            Command command
    ) {
        this.id = Objects.requireNonNull(id);
        validateTitle(title);
        this.title = title;
        this.description = description;
        this.workingDir = Objects.requireNonNull(workingDir);
        this.command = Objects.requireNonNull(command);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw ExceptionFactory.validation("Title of Script cannot be empty", "Script.title");
        }
    }

    private void changeWorkingDir(WorkingDir newWorkingDir) {
        this.workingDir = Objects.requireNonNull(newWorkingDir);
    }

    private void changeCommand(Command newCommand) {
        this.command = Objects.requireNonNull(newCommand);
    }

    public void update(
            String newTitle,
            String newDescription,
            WorkingDir workingDir,
            Command command
    ) {
        validateTitle(newTitle);
        this.title = newTitle;
        this.description = newDescription;
        changeWorkingDir(workingDir);
        changeCommand(command);
    }

    public ScriptId getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public WorkingDir getWorkingDir() {
        return this.workingDir;
    }

    public Command getCommand() {
        return this.command;
    }
}
