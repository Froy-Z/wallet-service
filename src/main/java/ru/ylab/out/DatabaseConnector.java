package ru.ylab.out;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс создания соединения с БД и управления миграциями.
 */
@NoArgsConstructor
public class DatabaseConnector {
    private static final String YLAB_SERVICE_SCHEMA = "ylab_service_schema" ;
    private static final HikariDataSource dataSource;

    static {
        Configuration configuration = Configuration.loadConfiguration();
        if (configuration != null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(configuration.getUrl());
            config.setUsername(configuration.getUsername());
            config.setPassword(configuration.getPassword());
            config.setMaximumPoolSize(3);

            dataSource = new HikariDataSource(config);
        } else {
            throw new RuntimeException("Ошибка получения данных конфигурационного файла.");
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }



    public static void addDatabaseChanges() throws SQLException {
        try (Connection connection = getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS ylab_service_schema");
            database.setDefaultSchemaName(YLAB_SERVICE_SCHEMA);

            // миграция создания схемы для служебной информации
            String changelogPath = Configuration.loadConfiguration().getChangelog();
            Liquibase liquibase = new Liquibase(changelogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        } catch (LiquibaseException | SQLException e) {
            e.printStackTrace();
        }
    }
}
