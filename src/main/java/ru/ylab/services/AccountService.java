package ru.ylab.services;

import ru.ylab.exceptions.BalanceChangeFailedException;
import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.in.factories.LoggingFactory;
import ru.ylab.interfaces.AccountInterface;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcAccount;
import ru.ylab.out.jdbc.JdbcTransaction;
import ru.ylab.out.repositories.AccountRepository;
import ru.ylab.out.repositories.TransactionRepository;

import java.math.BigDecimal;
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
    private final TransactionService transactionService = new TransactionService();
    private AccountRepository accountRepository;

    /**
     * Поиск счёта игрока
     *
     * @param accountList список всех счетов всех игроков
     * @param player      игрок, чей кошелёк ищем
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
     *
     * @param balance валидный баланс игрока
     */
    @Override
    public void printAccountBalance(BigDecimal balance) {
        System.out.printf("Счёт %.2f у.е. \n", balance);
    }

    /**
     * Кредитовая операция по пополнению баланса кошелька игрока.
     *
     * @param player  игрок для ведения логирования
     * @param account кошелёк игрока
     * @param amount  сумма к исполнению
     * @return лог о результате операции
     * @throws UnknownError если происходит неизвестная ошибка во время операции
     */
    @Override
    public Logging performOperation(Player player, Account account, BigDecimal amount, String type) throws InsufficientFundsException, SQLException {
        Long transactionId = null;
        Connection connection = null;
        try {
            if (type.equals("credit")) {
                transactionId = transactionService.conductingTransaction(account, amount, Transaction.TypeOperation.CREDIT); // создание транзакции и сохранение в БД
                return performCreditOperation(player, account, amount);
            } else {
                checkSufficientFunds(account, amount); // проверка достаточности средств
                transactionId = transactionService.conductingTransaction(account, amount, Transaction.TypeOperation.DEBIT); // создание транзакции и сохранение в БД
                return performDebitOperation(player, account, amount);
            }
        } catch (BalanceChangeFailedException | SQLException e) {
            connection = DatabaseConnector.getConnection();
            TransactionRepository transactionRepository = new JdbcTransaction(connection);
            transactionRepository.delete(transactionId); // удаление транзакции в случае ошибок
            return null;
        } finally {
            if(connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Logging performCreditOperation(Player player, Account account, BigDecimal amount) throws SQLException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            accountRepository = new JdbcAccount(connection);
            accountRepository.addAmount(account.getId(), amount); // пополнение баланса
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        System.out.printf("Баланс пополнен на %.2f y.e. \n", amount);
        return loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_CREDIT);
    }

    @Override
    public Logging performDebitOperation(Player player, Account account, BigDecimal amount) throws SQLException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            accountRepository = new JdbcAccount(connection);
            accountRepository.deductAmount(account.getId(), amount); // списание с баланса
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        System.out.printf("С баланса списано %.2f y.e. \n", amount);
        return loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_DEBIT);
    }

    /**
     * Проверка баланса на возможность снятия
     */
    private void checkSufficientFunds(Account account, BigDecimal amount) throws InsufficientFundsException, SQLException {
        BigDecimal balance;
        try (Connection connection = DatabaseConnector.getConnection()){
            accountRepository = new JdbcAccount(connection);
            balance = accountRepository.findBalanceById(account.getId());
            if (balance.doubleValue() < amount.doubleValue()) {
                throw new InsufficientFundsException("Не хватает средств для выполнения операции.");
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка соединения с БД для получения баланса.");
        }
    }

}
