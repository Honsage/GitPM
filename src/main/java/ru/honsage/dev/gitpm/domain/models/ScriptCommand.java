package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.valueobjects.ExecutableCommand;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptCommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.Objects;

public class ScriptCommand {
    private final ScriptCommandId id;
    private WorkingDir workingDir;
    private ExecutableCommand executableCommand;

    public ScriptCommand(
            ScriptCommandId id,
            WorkingDir workingDir,
            ExecutableCommand executableCommand
    ) {
        this.id = Objects.requireNonNull(id);
        this.workingDir = Objects.requireNonNull(workingDir);
        this.executableCommand = Objects.requireNonNull(executableCommand);
    }
}
