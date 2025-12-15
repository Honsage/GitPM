package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.repositories.ScriptRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.DatabaseManager;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ScriptEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers.ScriptEntityMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScriptRepositoryImpl implements ScriptRepository {
    private final DatabaseManager db;

    public ScriptRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Optional<Script> findById(ScriptId scriptId) {
        String query = "SELECT * FROM script WHERE id_script = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, scriptId.toString());

            ResultSet rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(
                    ScriptEntityMapper.toDomain(ScriptEntityMapper.fromResultSet(rs))
            );
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    @Override
    public List<Script> findByTitlePrefix(String titlePrefix, ProjectId id) {
        List<Script> list = new ArrayList<>();
        String query = "SELECT * FROM script WHERE id_project = ? AND title LIKE ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());
            st.setString(2, titlePrefix + "%");

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(ScriptEntityMapper.toDomain(ScriptEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public List<Script> findAllByProject(ProjectId id) {
        List<Script> list = new ArrayList<>();
        String query = "SELECT * FROM script WHERE id_project = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(ScriptEntityMapper.toDomain(ScriptEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public Script save(Script script, ProjectId id) {
        ScriptEntity entity = ScriptEntityMapper.toEntity(script, id);
        String query = """
                INSERT INTO script(id_script, id_project, title, description)
                VALUES (?, ?, ?, ?);""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.id());
            st.setString(2, id.toString());
            st.setString(3, entity.title());
            st.setString(4, entity.description());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return script;
    }

    @Override
    public Script update(Script script, ProjectId id) {
        ScriptEntity entity = ScriptEntityMapper.toEntity(script, id);
        String query = """
                UPDATE script
                SET title = ?, description = ?
                WHERE id_script = ?;""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.title());
            st.setString(2, entity.description());
            st.setString(3, entity.id());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return script;
    }

    @Override
    public void delete(ScriptId scriptId) {
        String query = "DELETE FROM script WHERE id_script = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, scriptId.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }
}
