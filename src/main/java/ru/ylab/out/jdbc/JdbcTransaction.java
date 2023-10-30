package ru.ylab.out.jdbc;

import ru.ylab.exceptions.NullPointerException;
import ru.ylab.exceptions.TransactionSaveException;
import ru.ylab.models.Transaction;
import ru.ylab.out.repositories.TransactionRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для взаимодействия с таблицей транзакций в базе данных посредством JDBC-драйвера
 */
public class JdbcTransaction implements TransactionRepository {
    private final Connection connection;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохранение объекта типа Transaction в базу данных;
     *
     * @param transaction объект-транзакция, переданная методу для сохранения.
     */
    @Override
    public Long save(Transaction transaction) throws TransactionSaveException {
        String sql = "INSERT INTO ylab_schema.transactions (id, player_id, account_id, transaction_uuid, execution_time, amount, type) " +
                "VALUES (nextval('ylab_schema.transactions_id_seq'), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, transaction.getPlayerId());
            statement.setLong(2, transaction.getAccountId());
            statement.setString(3, transaction.getTransactionId());
            statement.setTimestamp(4, transaction.getExecuteTime());
            statement.setBigDecimal(5, transaction.getAmount());
            statement.setObject(6, transaction.getType(), Types.OTHER);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Произошла ошибка сохранения.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1); // возврат сгенерированного последовательностью id транзакции
                } else {
                    connection.rollback();
                    throw new TransactionSaveException("Не удалось получить сгенерированный ключ.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new TransactionSaveException("Произошла ошибка сохранения транзакции.");
        }
    }

    @Override
    public List<String> findAllUUIDPlayer(Long playerId, Long accountId) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT transaction_uuid FROM ylab_schema.transactions WHERE player_id = ? AND account_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, playerId);
            statement.setLong(2, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String transactionId = resultSet.getString("transaction_uuid");

                    transactions.add(transactionId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении транзакций из базы данных.", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Метод поиска всех транзакций закреплённых за кошельком конкретного игрока.
     *
     * @param playerId  ID игрока;
     * @param accountId ID кошелька;
     * @return список транзакций.
     */
    @Override
    public List<Transaction> findAllTransactionsPlayer(Long playerId, Long accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM ylab_schema.transactions WHERE player_id = ? AND account_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, playerId);
            statement.setLong(2, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String transactionId = resultSet.getString("transaction_uuid");
                    Timestamp executionTime = resultSet.getTimestamp("execution_time");
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    Transaction.TypeOperation type = Transaction.TypeOperation.valueOf(resultSet.getString("type"));

                    transactions.add(new Transaction(playerId, accountId, transactionId, executionTime, amount, type));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении транзакций из базы данных.", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public void delete(Long account_id) {
        String sql = "DELETE FROM ylab_schema.transactions WHERE id = (SELECT MAX(id) FROM ylab_schema.transactions WHERE account_id = ?);";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, account_id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Произошла ошибка сохранения.");
            }

        } catch (SQLException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
