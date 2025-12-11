package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Connection connection;

    private DatabaseManager(String path) {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            initSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite", e);
        }
    }

    public static synchronized DatabaseManager getInstance(String path) {
        if (instance == null) {
            instance = new DatabaseManager(path);
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void initSchema() {
        try (InputStream input = getClass().getResourceAsStream(
                "/ru/honsage/dev/gitpm/db/sqlite/schema.sql"
        )) {
            if (input == null) {
                throw new RuntimeException("Db schema not found in resources");
            }

            String query = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            try (Statement st = connection.createStatement()) {
                st.executeUpdate(query);
            } catch (SQLException e) {
                throw new RuntimeException("DB error during query execution", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db schema", e);
        }
    }
}
