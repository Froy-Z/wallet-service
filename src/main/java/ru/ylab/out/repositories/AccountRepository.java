package ru.ylab.out.repositories;

import ru.ylab.models.Account;

import java.util.List;

public interface AccountRepository {
    void save(Account account);
    Account findById(Long accountId);
    List<Account> findAll();
    void delete(Long accountId);
}
