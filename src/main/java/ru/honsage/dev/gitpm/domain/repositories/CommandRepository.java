package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;

import java.util.List;
import java.util.Optional;

public interface CommandRepository {
    Optional<Command> findById(CommandId commandId);
    List<Command> findAllByScript(ScriptId id);

    Command save(Command command, ScriptId id);
    Command update(Command command, ScriptId id);
    void delete(CommandId commandId);
}
