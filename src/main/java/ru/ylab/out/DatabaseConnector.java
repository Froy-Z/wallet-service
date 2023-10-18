package ru.ylab.out;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс создания соединения с БД и управления миграциями.
 * Т.к. запрещено использовать внешние sql скрипты, одна транзакция на создание
 * служебной схемы в любом случае попадает в public, остальные согласно условию задачи.
 */
@NoArgsConstructor
public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:2345/ylab_db";
    private static final String USER = "ylab";
    private static final String PASSWORD = "123456";
    private static final String DEFAULT_SERVICE_SCHEMA = "ylab_service_schema";
    private static Connection connection;
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new SQLException("Произошла ошибка соединения с БД", e);
        }
    }

    public static void addDatabaseChanges() throws SQLException {
        try (Connection connection = getConnection()) {

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            // миграция создания схемы для служебной информации
            Liquibase liquibase = new Liquibase("liquibase/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (LiquibaseException | SQLException e) {
            e.printStackTrace();
        }
    }
}
