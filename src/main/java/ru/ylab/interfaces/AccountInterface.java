package ru.ylab.interfaces;

import ru.ylab.exceptions.BalanceChangeFailedException;
import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс для работы со счётом (кошельком) игроков.
 */
public interface AccountInterface {

    Account searchAccountPlayer(List<Account> accountList, Player player);
    /**
     * Печать баланса в консоль.
     * @param balance баланс конкретного игрока.
     */
    void printAccountBalance(BigDecimal balance);

    Logging performOperation(Player player, Account account, BigDecimal amount, String type) throws InsufficientFundsException, BalanceChangeFailedException, SQLException;

    /**
     * Кредитовая проводка для пополнения счетов игроков.
     * @param player игрок, чей счёт будет пополнен
     * @param account счёт(кошелёк)игрока для пополнения
     * @param amount сумма пополнения
     * @return возврат лога об удачной или неудачной операции
     */
    Logging performCreditOperation(Player player, Account account, BigDecimal amount) throws SQLException;

    /**
     * Дебетовая проводка для списания со счетов игроков.
     * @param player игрок, с чьего счёта будут списаны денежные средства
     * @param account счёт(кошелёк)игрока для списания
     * @param amount сумма списания
     * @return возврат лога об удачной или неудачной операции
     */
    Logging performDebitOperation(Player player, Account account, BigDecimal amount) throws SQLException;
}
