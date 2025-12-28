package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.valueobjects.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ScriptEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ScriptEntityMapper {
    public static ScriptEntity toEntity(Script script, ProjectId id) {
        return new ScriptEntity(
                script.getId().toString(),
                id.toString(),
                script.getTitle(),
                script.getDescription(),
                script.getWorkingDir().toString(),
                script.getCommand().toString()
        );
    }

    public static Script toDomain(ScriptEntity entity) {
        return new Script(
                ScriptId.fromString(entity.id()),
                entity.title(),
                entity.description(),
                new WorkingDir(entity.workingDir()),
                new Command(entity.command())
        );
    }

    public static ScriptEntity fromResultSet(ResultSet rs) throws SQLException {
        return new ScriptEntity(
                rs.getString("id_script"),
                rs.getString("id_project"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("working_dir"),
                rs.getString("command")
        );
    }
}
