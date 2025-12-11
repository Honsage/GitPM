package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite;

import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.domain.repositories.ProjectRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ProjectEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers.ProjectEntityMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepositoryImpl implements ProjectRepository {
    private final DatabaseManager db;

    public ProjectRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        String query = "SELECT * FROM projects WHERE id_project = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());

            ResultSet rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(
                    ProjectEntityMapper.toDomain(this.extractEntity(rs))
            );

        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    @Override
    public Optional<Project> findByLocalPath(LocalRepositoryPath localPath) {
        String query = "SELECT * FROM projects WHERE local_path = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, localPath.value());

            ResultSet rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(
                    ProjectEntityMapper.toDomain(this.extractEntity(rs))
            );

        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    @Override
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String query = "SELECT * FROM projects;";
        try (Statement st = db.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                list.add(ProjectEntityMapper.toDomain(this.extractEntity(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Project> findByTitlePrefix(String titlePrefix) {
        List<Project> list = new ArrayList<>();
        String query = "SELECT * FROM projects WHERE title LIKE ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, titlePrefix + "%");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(ProjectEntityMapper.toDomain(this.extractEntity(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Project> findWithRemote() {
        List<Project> list = new ArrayList<>();
        String query = "SELECT * FROM projects WHERE remote_url IS NOT NULL;";
        try (Statement st = db.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                list.add(ProjectEntityMapper.toDomain(this.extractEntity(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = ProjectEntityMapper.toEntity(project);
        String query = """
                INSERT INTO projects(id_project, title, description, local_path, remote_url, added_at)
                VALUES (?, ?, ?, ?, ?, ?);""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.id());
            st.setString(2, entity.title());
            st.setString(3, entity.description());
            st.setString(4, entity.localPath());
            st.setString(5, entity.remoteURL());
            st.setString(6, entity.addedAt());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return project;
    }

    @Override
    public Project update(Project project) {
        ProjectEntity entity = ProjectEntityMapper.toEntity(project);
        String query = """
                UPDATE projects
                SET title = ?, description = ?, remote_url = ?
                WHERE id_project = ?;""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.title());
            st.setString(2, entity.description());
            st.setString(3, entity.remoteURL());
            st.setString(4, entity.id());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return project;
    }

    @Override
    public void delete(ProjectId id) {
        String query = "DELETE FROM projects WHERE id_project = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    private ProjectEntity extractEntity(ResultSet rs) throws SQLException {
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
