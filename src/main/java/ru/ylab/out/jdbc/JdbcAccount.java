package ru.ylab.out.jdbc;

import ru.ylab.models.Account;
import ru.ylab.out.repositories.AccountRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс для взаимодействия с таблицей счетов(кошельков)
 * игроков в базе данных посредством JDBC-драйвера.
 */
public class JdbcAccount implements AccountRepository {
    private final Connection connection;
    public JdbcAccount(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохранение объекта типа Account в базу данных;
     *
     * @param account объект-счёт(кошелёк), переданный методу для сохранения.
     */
    @Override
    public void save(Account account) {
        String sql = "INSERT INTO ylab_schema.accounts (id, player_id, balance) " +
                "VALUES (nextval('accounts_id_seq'), ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, account.getPlayerId());
            statement.setDouble(2, account.getBalance());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Произошла ошибка сохранения.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Не удалось получить сгенерированный ключ.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addAmount(Long accountId, double amount) {
        String sql = "UPDATE ylab_schema.accounts SET balance = balance + ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setLong(2, accountId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Произошла ошибка при попытке пополнить баланс.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deductAmount(Long accountId, double amount) {
        String sql = "UPDATE ylab_schema.accounts SET balance = balance - ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, amount);
            statement.setLong(2, accountId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Произошла ошибка при попытке снять средства с баланса.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Account findById(Long accountId) {
        String sql = "SELECT * FROM ylab_schema.accounts WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long playerId = resultSet.getLong("player_id");
                    double balance = resultSet.getDouble("balance");
                    Account account = new Account(accountId, playerId, balance);
                    account.setBalance(balance);
                    return account;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double findBalanceById(Long accountId) throws RuntimeException, SQLException {
        String sql = "SELECT balance FROM ylab_schema.accounts WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getDouble("balance");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при получении баланса игрока из базы данных.", e);
        } catch (SQLException e) {
            throw new SQLException("Произошла ошибка соединения с БД", e);
        }
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM ylab_schema.accounts";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long player_id = resultSet.getLong("player_id");
                double balance = resultSet.getDouble("balance");
                accounts.add(new Account(id, player_id, balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableList(accounts);
    }

    @Override
    public void delete(Long accountId) {
        String sql = "DELETE FROM ylab_schema.accounts WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, accountId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
