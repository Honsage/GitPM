package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories;

import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.repositories.CommandRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.CommandId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.DatabaseManager;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.CommandEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.entities.ScriptEntity;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers.CommandEntityMapper;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.mappers.ScriptEntityMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandRepositoryImpl implements CommandRepository {
    private final DatabaseManager db;

    public CommandRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Optional<Command> findById(CommandId commandId) {
        String query = "SELECT * FROM command WHERE id_command = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, commandId.toString());

            ResultSet rs = st.executeQuery();
            if (!rs.next()) return Optional.empty();

            return Optional.of(
                    CommandEntityMapper.toDomain(CommandEntityMapper.fromResultSet(rs))
            );
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }

    @Override
    public List<Command> findAllByScript(ScriptId id) {
        List<Command> list = new ArrayList<>();
        String query = "SELECT * FROM command WHERE id_script = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, id.toString());

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(CommandEntityMapper.toDomain(CommandEntityMapper.fromResultSet(rs)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return list;
    }

    @Override
    public Command save(Command command, ScriptId id) {
        CommandEntity entity = CommandEntityMapper.toEntity(command, id);
        String query = """
                INSERT INTO command(id_command, id_script, working_dir, executable_command, order)
                VALUES (?, ?, ?, ?, ?);""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.id());
            st.setString(2, id.toString());
            st.setString(3, entity.workingDir());
            st.setString(4, entity.executableCommand());
            st.setInt(5, entity.order());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return command;
    }

    @Override
    public Command update(Command command, ScriptId id) {
        CommandEntity entity = CommandEntityMapper.toEntity(command, id);
        String query = """
                UPDATE command
                SET working_dir = ?, executable_command = ?, order = ?
                WHERE id_command = ?;""";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, entity.workingDir());
            st.setString(2, entity.executableCommand());
            st.setInt(3, entity.order());
            st.setString(3, entity.id());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
        return command;
    }

    @Override
    public void delete(CommandId commandId) {
        String query = "DELETE FROM command WHERE id_command = ?;";
        try (PreparedStatement st = db.getConnection().prepareStatement(query)) {
            st.setString(1, commandId.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error during query execution", e);
        }
    }
}
