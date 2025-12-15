package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers;

import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.valueobjects.*;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.CommandEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ScriptEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandEntityMapper {
    public static CommandEntity toEntity(Command command, ScriptId id) {
        return new CommandEntity(
                command.getId().toString(),
                id.toString(),
                command.getWorkingDir().toString(),
                command.getExecutableCommand().toString(),
                command.getOrder()
        );
    }

    public static Command toDomain(CommandEntity entity) {
        return new Command(
                CommandId.fromString(entity.id()),
                new WorkingDir(entity.workingDir()),
                ExecutableCommand.parse(entity.executableCommand()),
                entity.order()
        );
    }

    public static CommandEntity fromResultSet(ResultSet rs) throws SQLException {
        return new CommandEntity(
                rs.getString("id_command"),
                rs.getString("id_script"),
                rs.getString("working_dir"),
                rs.getString("executable_command"),
                rs.getInt("order")
        );
    }
}
