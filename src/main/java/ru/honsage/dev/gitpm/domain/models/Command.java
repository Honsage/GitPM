package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.valueobjects.ExecutableCommand;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.Objects;

public class Command {
    private final CommandId id;
    private WorkingDir workingDir;
    private ExecutableCommand executableCommand;
    private int order;

    public Command(
            CommandId id,
            WorkingDir workingDir,
            ExecutableCommand executableCommand,
            int order
    ) {
        this.id = Objects.requireNonNull(id);
        this.workingDir = Objects.requireNonNull(workingDir);
        this.executableCommand = Objects.requireNonNull(executableCommand);
        validateOrder(order);
        this.order = order;
    }

    public Command(
            CommandId id,
            WorkingDir workingDir,
            ExecutableCommand executableCommand
    ) {
        this(id, workingDir, executableCommand, 0);
    }

    private void validateOrder(int order) {
        if (order < 0) {
            throw ExceptionFactory.validation(
                    "Command order must be non-negative integer",
                    "Command.order"
            );
        }
    }
}
