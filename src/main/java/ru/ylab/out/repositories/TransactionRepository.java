package ru.ylab.out.repositories;

import ru.ylab.exceptions.TransactionSaveException;
import ru.ylab.models.Transaction;

import java.util.List;

public interface TransactionRepository {
    Long save(Transaction transaction) throws TransactionSaveException;
    void delete(Long account_id);

    List<Transaction> findAllTransactionsPlayer(Long playerId, Long accountId);
    List<String> findAllUUIDPlayer(Long playerId, Long accountId);

}
