package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.valueobjects.ExecutableCommand;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.Objects;

public class ScriptCommand {
    private final CommandId id;
    private WorkingDir workingDir;
    private ExecutableCommand executableCommand;

    public ScriptCommand(
            CommandId id,
            WorkingDir workingDir,
            ExecutableCommand executableCommand
    ) {
        this.id = Objects.requireNonNull(id);
        this.workingDir = Objects.requireNonNull(workingDir);
        this.executableCommand = Objects.requireNonNull(executableCommand);
    }
}
