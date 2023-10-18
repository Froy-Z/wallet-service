package ru.ylab.interfaces;

import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

/**
 * Интерфейс для работы со счётом (кошельком) игроков.
 */
public interface AccountInterface {
    /**
     * Печать баланса в консоль.
     * @param balance баланс конкретного игрока.
     */
    void printAccountBalance(double balance);

    /**
     * Кредитовая проводка для пополнения счетов игроков.
     * @param player игрок, чей счёт будет пополнен
     * @param account счёт(кошелёк)игрока для пополнения
     * @param amount сумма пополнения
     * @return возврат лога об удачной или неудачной операции
     */
    Logging performCreditTransaction(Player player, Account account, double amount)
    ;
    /**
     * Дебетовая проводка для списания со счетов игроков.
     * @param player игрок, с чьего счёта будут списаны денежные средства
     * @param account счёт(кошелёк)игрока для списания
     * @param amount сумма списания
     * @return возврат лога об удачной или неудачной операции
     */
    Logging performDebitTransaction(Player player, Account account, double amount);
}
