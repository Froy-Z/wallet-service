package ru.ylab.interfaces;

import ru.ylab.models.Account;

/**
 * Интерфейс для печати транзакций.
 */
public interface TransactionInterface {
    /**
     * Печать всех транзакций, с указанием времени и типа операции.
     * @param account счёт (кошелёк), история которого будет выведена в консоль
     */
    void printTransaction(Account account) ;
}
