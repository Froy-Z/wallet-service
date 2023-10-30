package ru.ylab.out.jdbc;

import ru.ylab.models.Player;
import ru.ylab.out.repositories.PlayerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс для взаимодействия с таблицей игроков в базе данных посредством JDBC-драйвера
 */
public class JdbcPlayer implements PlayerRepository {
    private final Connection connection;
    public JdbcPlayer(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохранение объекта типа Player в базу данных;
     *
     * @param player объект-игрок, переданный методу для сохранения.
     */
    @Override
    public Long save(Player player) {
        String sql = "INSERT INTO ylab_schema.players (id, login, password) VALUES (nextval('ylab_schema.players_id_seq'),?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, player.getLogin());
            statement.setString(2, player.getPassword());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Произошла ошибка сохранения.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1); // возврат сгенерированного пследовательностью id игрока
                } else {
                    throw new SQLException("Не удалось получить сгенерированный ключ.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Логируем ошибку для дальнейшего анализа
        }
        return null;
    }

    @Override
    public Player findById(Long playerId) {
        String sql = "SELECT * FROM ylab_schema.players WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    return new Player(login, password);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM ylab_schema.players";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                players.add(new Player(id, login, password));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Collections.unmodifiableList(players);
    }

    @Override
    public void delete(Long playerId) {
        String sql = "DELETE FROM ylab_schema.players WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, playerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
