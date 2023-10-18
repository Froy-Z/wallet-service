package ru.ylab.out.jdbc;

import ru.ylab.exceptions.NullPointerException;
import ru.ylab.models.Transaction;
import ru.ylab.out.repositories.TransactionRepository;

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
    public void save(Transaction transaction) {
        String sql = "INSERT INTO ylab_schema.transactions (player_id, account_id, transaction_uuid, execution_time, amount, type) " +
                "VALUES (nextval('transactions_id_seq'), ?, ?, gen_random_uuid(), ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, transaction.getAccountId());
            statement.setString(2, transaction.getTransactionId());
            statement.setTimestamp(3, transaction.getExecuteTime());
            statement.setDouble(4, transaction.getAmount());
            statement.setObject(5, transaction.getType());

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
                    Long id = resultSet.getLong("id");
                    String transactionId = resultSet.getString("transaction_uuid");
                    Timestamp executionTime = resultSet.getTimestamp("execution_time");
                    double amount = resultSet.getDouble("amount");
                    Transaction.TypeOperation type = (Transaction.TypeOperation) resultSet.getObject("type");

                    transactions.add(new Transaction(id, playerId, accountId, transactionId, executionTime, amount, type));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении транзакций из базы данных.", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
