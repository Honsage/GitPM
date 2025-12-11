package ru.honsage.dev.gitpm.infrastructure.persistence.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Connection connection;

    private DatabaseManager(String path) {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite" + path);
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
}
