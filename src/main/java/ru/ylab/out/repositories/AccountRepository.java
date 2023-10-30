package ru.ylab.out.repositories;

import ru.ylab.models.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface AccountRepository {

    Long save(Account account);
    Account findById(Long accountId);
    List<Account> findAll();
    void delete(Long accountId);
    void deductAmount(Long accountId, BigDecimal amount);
    BigDecimal findBalanceById(Long accountId) throws RuntimeException, SQLException;
    void addAmount(Long accountId, BigDecimal amount);
}
