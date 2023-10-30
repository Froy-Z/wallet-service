package ru.ylab.interfaces;

import ru.ylab.models.Account;
import ru.ylab.models.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Интерфейс для печати транзакций.
 */
public interface TransactionInterface {
    /**
     * Печать всех транзакций, с указанием времени и типа операции.
     * @param account счёт (кошелёк), история которого будет выведена в консоль
     */
    void printTransaction(Account account);

    Long conductingTransaction(Account account, BigDecimal amount, Transaction.TypeOperation typeOperation) throws RuntimeException, SQLException;
}
