package ru.ylab.interfaces;

import ru.ylab.models.Player;
import ru.ylab.models.Transaction;

import java.util.List;

public interface ITransaction {
    void printTransaction(List<Transaction> transactionList, Player player);
}
