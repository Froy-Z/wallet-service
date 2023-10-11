package ru.ylab.services;

import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.exceptions.NotFoundException;
import ru.ylab.exceptions.NullPointerException;
import ru.ylab.in.factories.TransactionFactory;
import ru.ylab.interfaces.IAccount;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;

import java.util.List;

public class AccountService implements IAccount {

    /**
     * Печать в консоль текущий баланс игрока
     *
     * @param account кошелёк
     */

    @Override
    public void getAccountBalance(Account account) {
        System.out.printf("Счёт %.2f у.е. \n", account.getBalance());
    }

    // уникальность транзакции уже проверена фабрикой транзакций

    /**
     * Кредитовая операция по пополнению баланса кошелька игрока.
     *
     * @param accountList коллекция всех кошельков всех игроков
     * @param logList     коллекция логов всех игроков
     * @param accountId   номер ID кошелька
     * @param amount      сумма к исполнению
     */
    @Override
    public void performDebitTransaction(List<Account> accountList, List<Logging> logList, List<Transaction> transactionList,
                                        Player player, Long accountId, double amount) throws InsufficientFundsException, NullPointerException {

        Account account = checkAccount(accountList, accountId);

        Long playerId = player.getId();

        TransactionFactory transactionFactory = new TransactionFactory();

        if (account.getBalance() >= amount) {

            transactionFactory.makeTransactionAndSave(transactionList, player, amount, Logging.TypeOperation.SUCCES_DEBIT);
            account.setBalance(account.getBalance() - amount);
            logList.add(new Logging(playerId, Logging.TypeOperation.SUCCES_DEBIT));

            System.out.printf("Списано со счёта %.2f y.e. \n", amount);
        } else {

            transactionFactory.makeTransactionAndSave(transactionList, player, amount, Logging.TypeOperation.FAIL_DEBIT);
            logList.add(new Logging(playerId, Logging.TypeOperation.FAIL_DEBIT));


            throw new InsufficientFundsException(
                    String.format(
                            "Недостаточно средств. Уменьшите запрашиваемую сумму минимум на %.2f y.e. \n",
                            Math.round((amount - account.getBalance()) * 100.0) / 100.0)
            );
        }
    }

    /**
     * Кредитовая операция по пополнению баланса кошелька игрока.
     *
     * @param accountList коллекция всех кошельков всех игроков
     * @param logList     коллекция логов всех игроков
     * @param accountId   номер ID кошелька
     * @param amount      сумма к исполнению
     * @throws UnknownError если происходит неизвестная ошибка во время операции
     */
    @Override
    public void performCreditTransaction(List<Account> accountList, List<Logging> logList, List<Transaction> transactionList,
                                         Player player, Long accountId, double amount) throws UnknownError {

        Account account = checkAccount(accountList, accountId);

        Long playerId = player.getId();
        TransactionFactory transactionFactory = new TransactionFactory();

        try {


            /*
             * создание уникальной транзакции и лога для исполнения операции и добавление в историю
             */
            transactionFactory.makeTransactionAndSave(transactionList, player, amount, Logging.TypeOperation.SUCCES_CREDIT);
            account.setBalance(account.getBalance() + amount); // изменение баланса
            logList.add(new Logging(playerId, Logging.TypeOperation.SUCCES_CREDIT));

            System.out.printf("Пополнение счёта на %.2f y.e. \n", amount);

        } catch (UnknownError e) {

            /*
             * создание уникальной транзакции и лога для исполнения операции и добавление в историю в случае исключения
             */
            transactionFactory.makeTransactionAndSave(transactionList, player, amount, Logging.TypeOperation.FAIL_CREDIT);
            logList.add(new Logging(playerId, Logging.TypeOperation.FAIL_CREDIT));
            System.out.println("Во время операции произошла неизвестная ошибка. \n");
            System.out.println(e.getMessage());

        }
    }

    /**
     * Поиск счёта игрока
     *
     * @param accountList список всех счетов всех игроков
     * @param playerId    поиск кошелька по id игрока
     * @return кошелёк игрока или null
     */
    public Account searchAccountPlayer(List<Account> accountList, Long playerId) {
        return accountList.stream()
                .filter(account -> account.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Дополнительная проверка кошелька
     *
     * @param accountList список всех счетов всех игроков
     * @param accountId ID игрока
     * @return кошелёк игрока
     */

    private Account checkAccount(List<Account> accountList, Long accountId) {
        try {
            return accountList
                    .stream()
                    .filter(account1 -> account1.getPlayerId().equals(accountId))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException(
                                    String.format(
                                            "Account with ID %d not found.",
                                            accountId
                                    )
                            )
                    );
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
