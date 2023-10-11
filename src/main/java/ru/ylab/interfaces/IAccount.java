package ru.ylab.interfaces;

import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;

import java.util.List;

public interface IAccount {
    void getAccountBalance(Account account);
    void performDebitTransaction(List<Account> accountList, List<Logging> logList, List<Transaction> transactionList, Player player, Long accountId, double amount)  throws InsufficientFundsException;
    void performCreditTransaction(List<Account> accountList, List<Logging> logList, List<Transaction> transactionList, Player player, Long accountId, double amount);

}
