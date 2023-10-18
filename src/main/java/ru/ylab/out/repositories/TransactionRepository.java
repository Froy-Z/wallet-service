package ru.ylab.out.repositories;

import ru.ylab.models.Transaction;

import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction);
    List<Transaction> findAllTransactionsPlayer(Long playerId, Long accountId);
}
