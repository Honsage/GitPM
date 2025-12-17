package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.repositories.CommandRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.ExecutableCommand;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.List;

public class CommandService {
    private final CommandRepository commandRepository;

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public Command createCommand(
            ScriptId scriptId,
            WorkingDir workingDir,
            ExecutableCommand executableCommand,
            int order
    ) {
        Command command = new Command(
                CommandId.random(),
                workingDir,
                executableCommand,
                order
        );
        return commandRepository.save(command, scriptId);
    }

    public Command createSingleCommand(
            ScriptId scriptId,
            WorkingDir workingDir,
            ExecutableCommand executableCommand
    ) {
        Command command = new Command(
                CommandId.random(),
                workingDir,
                executableCommand
        );
        return commandRepository.save(command, scriptId);
    }

    public Command getCommand(CommandId commandId) {
        return commandRepository.findById(commandId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Command", commandId.toString()));
    }

    public List<Command> getAllCommands(ScriptId scriptId) {
        return commandRepository.findAllByScript(scriptId);
    }

    public Command updateCommand(
            ScriptId scriptId,
            CommandId commandId,
            WorkingDir newWorkingDir,
            ExecutableCommand newExecutableCommand,
            int newOrder
    ) {
        Command command = getCommand(commandId);

        command.update(
                newWorkingDir,
                newExecutableCommand,
                newOrder
        );

        return commandRepository.update(command, scriptId);
    }

    public void deleteCommand(CommandId commandId) {
        commandRepository.delete(commandId);
    }
}
