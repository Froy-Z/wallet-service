package ru.ylab.in.factories;

import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;

import java.util.List;

public class TransactionFactory {

    /**
     * Создать транзакцию и сохранить в общую коллекцию транзакций игрока
     * @param transactionHistory коллекция транзакций игрока
     * @param player игрок
     * @param amount сумма к исполнению
     */
    public void makeTransactionAndSave(List<Transaction> transactionHistory, Player player, double amount, Logging.TypeOperation type) {
        Transaction transaction = new Transaction(player.getId(), amount, type);

        boolean nameExists = transactionHistory.stream()
                .anyMatch(player1 -> player1.getTransactionId().equals(transaction.getTransactionId()));

        // рекурсия без счётчика, т.к. маловероятно, что будет хотя бы 1 совпадение
        if(!nameExists) {
            transactionHistory.add(transaction);
        } else {
            makeTransactionAndSave(transactionHistory, player, amount, type);
        }
    }
}
