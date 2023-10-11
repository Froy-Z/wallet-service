package ru.ylab.services;

import ru.ylab.interfaces.ITransaction;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class TransactionService implements ITransaction {

    /**
     * Вывод в консоль информации о денежных транзакциях игрока
     * @param transactionList список транзакций
     * @param player игрок для получения ID
     */
    public void printTransaction(List<Transaction> transactionList, Player player) {

        if (!transactionList.isEmpty()) {

            transactionList.stream()
                    .filter(transaction -> transaction.getPlayerId().equals(player.getId()) &&
                            (transaction.getType() == Logging.TypeOperation.SUCCES_DEBIT ||
                                    transaction.getType() == Logging.TypeOperation.SUCCES_CREDIT ||
                                    transaction.getType() == Logging.TypeOperation.FAIL_CREDIT ||
                                    transaction.getType() == Logging.TypeOperation.FAIL_DEBIT))
                    .sorted(Comparator.comparing(Transaction::getExecuteTime))  // Сортировка по времени выполнения
                    .forEach(transaction -> {
                        LocalDateTime executionTime = transaction.getExecuteTime().truncatedTo(ChronoUnit.SECONDS);
                        String operationType = transaction.getType().name();
                        double amount = transaction.getAmount();
                        System.out.println("- Время выполнения: \t" + executionTime + "\t, Тип операции: " + operationType + "\t, Сумма: " + amount);
                    });
        } else {
            System.out.println("Список транзакций пуст!\n");
        }
    }
}
