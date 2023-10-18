package ru.ylab.services;

import ru.ylab.interfaces.TransactionInterface;
import ru.ylab.models.Account;
import ru.ylab.models.Transaction;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

public class TransactionService implements TransactionInterface {
    JdbcTransaction jdbcTransaction;

    /**
     * Вывод в консоль информации о денежных транзакциях игрока
     * @param account кошелёк игрока
     */
    public void printTransaction(Account account) {
        Long playerId = account.getPlayerId();
        Long accountId = account.getId();

        try (Connection connection = DatabaseConnector.getConnection()) {
            jdbcTransaction = new JdbcTransaction(connection);
            List<Transaction> transactionList = jdbcTransaction.findAllTransactionsPlayer(playerId, accountId);

            if (!transactionList.isEmpty()) {
                transactionList.stream()
                        .sorted(Comparator.comparing(Transaction::getExecuteTime)) // сортировка по времени выполнения транзакции
                        .forEach(transaction -> {
                            Timestamp executionTime = transaction.getExecuteTime(); // получение времени исполнения
                            String operationType = transaction.getType().name(); // получение типа операции
                            double amount = transaction.getAmount(); // получение суммы к исполнению по балансу
                            System.out.println("- Время выполнения: " + executionTime + ", Тип операции: " + operationType + ", Сумма: " + amount);
                        });
            } else {
                System.out.println("Список транзакций пуст!\n");
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка соединения с БД");
        }
    }
}
