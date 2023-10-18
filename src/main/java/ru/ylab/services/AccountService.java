package ru.ylab.services;

import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.in.factories.LoggingFactory;
import ru.ylab.in.factories.TransactionFactory;
import ru.ylab.interfaces.AccountInterface;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcAccount;
import ru.ylab.out.jdbc.JdbcTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс сервис для взаимодействия с Account-объектами,
 * такие, как поиск счетов, печать баланса, проводка по
 * счёту дебетовой и кредитовой операции, а также вспомогательные методы
 */
public class AccountService implements AccountInterface {
    private final LoggingFactory loggingFactory = new LoggingFactory();
    private final TransactionFactory transactionFactory = new TransactionFactory();
    private JdbcAccount jdbcAccount;
    private JdbcTransaction jdbcTransaction;

    /**
     * Поиск счёта игрока
     * @param accountList список всех счетов всех игроков
     * @param player игрок, чей кошелёк ищем
     * @return кошелёк игрока или null
     */
    public Account searchAccountPlayer(List<Account> accountList, Player player) {
        Long playerId = player.getId();
        return accountList.stream()
                .filter(account -> account.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Печать в консоль текущего баланса игрока
     * @param balance валидный баланс игрока
     */
    @Override
    public void printAccountBalance(double balance) {
        System.out.printf("Счёт %.2f у.е. \n", balance);
    }

    /**
     * Кредитовая операция по пополнению баланса кошелька игрока.
     *
     * @param player игрок для ведения логирования
     * @param account кошелёк игрока
     * @param amount  сумма к исполнению
     * @return лог о результате операции
     * @throws UnknownError если происходит неизвестная ошибка во время операции
     */
    @Override
    public Logging performCreditTransaction(Player player, Account account, double amount) throws UnknownError {
        try (Connection connection = DatabaseConnector.getConnection()){
            jdbcAccount = new JdbcAccount(connection); // соединение с БД для работы с кошельками
            jdbcTransaction = new JdbcTransaction(connection); // соединение с БД для работы с транзакциями
            performCredit(account, amount);
            System.out.printf("Баланс пополнен на %.2f y.e. \n", amount);
            return loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_CREDIT);
        } catch (SQLException e) {
            System.out.println(("Выполнение прервано из-за ошибки. Операция отменена."));
            return loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_DEBIT);
        }
    }

    /**
     * Дебетовая операция по снятию денежных средств с баланса кошелька игрока.
     *
     * @param player игрок для ведения логирования
     * @param account кошелёк игрока
     * @param amount  сумма к исполнению
     * @return лог о результате операции
     * @throws InsufficientFundsException обработка исключения недостаточности средств на счету игрока
     */
    @Override
    public Logging performDebitTransaction(Player player, Account account, double amount) throws RuntimeException {

        try (Connection connection = DatabaseConnector.getConnection()) {
            jdbcAccount = new JdbcAccount(connection); // соединение с БД для работы с кошельками
            jdbcTransaction = new JdbcTransaction(connection); // соединение с БД для работы с транзакциями

            if (checkSufficientFunds(account.getBalance(), amount)) {
                performDebit(account, amount);
                System.out.printf("Списано со счёта %.2f y.e. \n", amount);
                return loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_DEBIT);
            } else {
                handleInsufficientFundsException(amount, account.getBalance());
                return loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_DEBIT);
            }
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
            return loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_DEBIT);
        } catch (SQLException e) {
            System.out.println(("Выполнение прервано из-за ошибки. Операция отменена."));
            return loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_DEBIT);
        }
    }

    /**
     * Проверка баланса на возможность снятия
     * @param balance баланс игрока
     * @param amount сумма к списанию
     * @return булево значение
     */
    private boolean checkSufficientFunds(double balance, double amount) {
        return balance >= amount;
    }

    /**
     * Выполнение операции снятия с баланса
     * @param account кошелёк игрока
     * @param amount сумма к списанию
     */
    private void performDebit(Account account, double amount) {
        jdbcAccount.deductAmount(account.getId(), amount);
        jdbcTransaction.save(transactionFactory.makeTransaction(account, amount, Transaction.TypeOperation.SUCCES_DEBIT));
    }

    private void performCredit(Account account, double amount) {
        jdbcAccount.addAmount(account.getId(), amount);
        jdbcTransaction.save(transactionFactory.makeTransaction(account, amount, Transaction.TypeOperation.SUCCES_DEBIT));
    }

    /**
     * Обработка разницы числового выражения
     * @param amount сумма к списанию
     * @param balance баланс игрока
     * @throws InsufficientFundsException пробрасывание исключения недостаточности средств
     */
    private void handleInsufficientFundsException(double amount, double balance) throws InsufficientFundsException {
        double requiredAmount = Math.round((amount - balance) * 100.0) / 100.0;
        throw new InsufficientFundsException(
                String.format(
                        "Недостаточно средств. Уменьшите запрашиваемую сумму минимум на %.2f y.e. \n",
                        requiredAmount)
        );
    }
}
