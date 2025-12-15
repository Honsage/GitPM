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

    public void changeOrder(int newOrder) {
        validateOrder(newOrder);
        this.order = newOrder;
    }

    public void update(
            WorkingDir newWorkingDir,
            ExecutableCommand newExecutableCommand,
            int newOrder
    ) {
        this.workingDir = Objects.requireNonNull(newWorkingDir);
        this.executableCommand = Objects.requireNonNull(newExecutableCommand);
        changeOrder(newOrder);
    }

    public CommandId getId() {
        return this.id;
    }

    public WorkingDir getWorkingDir() {
        return this.workingDir;
    }

    public ExecutableCommand getExecutableCommand() {
        return this.executableCommand;
    }

    public int getOrder() {
        return this.order;
    }
}
