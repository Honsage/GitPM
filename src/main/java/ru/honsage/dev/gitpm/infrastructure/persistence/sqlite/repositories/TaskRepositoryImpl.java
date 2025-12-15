package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories;

import ru.honsage.dev.gitpm.domain.models.Task;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.repositories.TaskRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.TaskId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.DatabaseManager;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.TaskEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers.TaskEntityMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepositoryImpl implements TaskRepository {
    private final DatabaseManager db;

    public TaskRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Optional<Task> findById(TaskId taskId) {
        String query = "SELECT * FROM task WHERE id_task = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, taskId.toString());

            ResultSet rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(
                    TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs))
            );
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    @Override
    public List<Task> findByTitlePrefix(String titlePrefix, ProjectId id) {
        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM task WHERE id_project = ? AND title LIKE ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.setString(2, titlePrefix + "%");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Task> findAllByProject(ProjectId id) {
        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM task WHERE id_project = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Task> findByCompleted(boolean isCompleted, ProjectId id) {
        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM task WHERE id_project = ? AND is_completed = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.setInt(2, isCompleted? 1 : 0);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Task> findByPriority(TaskPriority priority, ProjectId id) {
        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM task WHERE id_project = ? AND priority = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.setString(2, priority.toString());

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Task> findOverdue(ProjectId id) {
        List<Task> list = new ArrayList<>();
        String query = """
                SELECT *
                FROM task
                WHERE id_project = ?
                AND is_completed = 0
                AND deadline_at IS NOT NULL
                AND deadline_at < ?
                ORDER BY deadline_at;""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.setString(2, LocalDateTime.now().toString());

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(TaskEntityMapper.toDomain(TaskEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public Task save(Task task, ProjectId id) {
        TaskEntity entity = TaskEntityMapper.toEntity(task, id);
        String query = """
                INSERT INTO task(id_task, id_project, title, content, created_at, is_completed, deadline_at, priority)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.id());
            st.setString(2, id.toString());
            st.setString(3, entity.title());
            st.setString(4, entity.content());
            st.setString(5, entity.createdAt());
            st.setInt(6, entity.isCompleted());
            st.setString(7, entity.deadlineAt());
            st.setString(8, entity.priority());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return task;
    }

    @Override
    public Task update(Task task, ProjectId id) {
        TaskEntity entity = TaskEntityMapper.toEntity(task, id);
        String query = """
                UPDATE task
                SET title = ?, content = ?, is_completed = ?, deadline_at = ?, priority = ?
                WHERE id_task = ?;""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.title());
            st.setString(2, entity.content());
            st.setInt(3, entity.isCompleted());
            st.setString(4, entity.deadlineAt());
            st.setString(5, entity.priority());
            st.setString(6, entity.id());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return task;
    }

    @Override
    public void delete(TaskId taskId) {
        String query = "DELETE FROM task WHERE id_task = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, taskId.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }
}
