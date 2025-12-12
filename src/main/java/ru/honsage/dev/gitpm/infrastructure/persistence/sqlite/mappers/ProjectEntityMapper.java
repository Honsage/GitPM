package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers;

import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.domain.valueobjects.GitRemoteURL;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ProjectEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ProjectEntityMapper {
    public static ProjectEntity toEntity(Project project) {
        return new ProjectEntity(
                project.getId().toString(),
                project.getTitle(),
                project.getDescription(),
                project.getLocalPath().value(),
                project.getRemoteURL() == null ? null : project.getRemoteURL().value(),
                project.getAddedAt().toString()

        );
    }

    public static Project toDomain(ProjectEntity entity) {
        return new Project(
                ProjectId.fromString(entity.id()),
                entity.title(),
                entity.description(),
                new LocalRepositoryPath(entity.localPath()),
                entity.remoteURL() == null ? null : new GitRemoteURL(entity.remoteURL()),
                LocalDateTime.parse(entity.addedAt())
        );
    }

    public static ProjectEntity fromResultSet(ResultSet rs) throws SQLException {
        return new ProjectEntity(
                rs.getString("id_project"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("local_path"),
                rs.getString("remote_url"),
                rs.getString("added_at")
        );
    }
}
