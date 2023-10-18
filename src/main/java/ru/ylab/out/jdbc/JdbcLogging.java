package ru.ylab.out.jdbc;

import ru.ylab.exceptions.NullPointerException;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.out.repositories.LoggingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для взаимодействия с таблицей логов в базе данных посредством JDBC-драйвера
 */
public class JdbcLogging implements LoggingRepository {
    private final Connection connection;
    public JdbcLogging(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохранение объекта типа Logging в базу данных;
     *
     * @param logging объект-лога, переданный методу для сохранения.
     */
    @Override
    public void save(Logging logging) {
        String sql = "INSERT INTO ylab_schema.loggingies (id ,player_id, execution_time, type) " +
                     "VALUES (nextval('loggingies_id_seq'), ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, logging.getPlayerId());
            statement.setTimestamp(2, logging.getExecutionTime());
            statement.setObject(3, logging.getType());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Произошла ошибка сохранения.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Не удалось получить сгенерированный ключ.");
                }
            }
        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Logging> findAllLoggingPlayer(Player player) {
        List<Logging> loggingies = new ArrayList<>();
        Long playerId = player.getId();

        String sql = "SELECT * FROM ylab_schema.loggingies WHERE player_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    Timestamp executionTime = resultSet.getTimestamp("execution_time");
                    Logging.TypeOperation type = (Logging.TypeOperation) resultSet.getObject("type");

                    loggingies.add(new Logging(id, playerId, executionTime, type));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении аудита из базы данных.", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return loggingies;
    }
}
