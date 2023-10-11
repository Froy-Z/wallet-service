package ru.ylab.models;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private final Long accountId;
    private final Long playerId;
    private double balance;
    private final List<Transaction> transactionList = new ArrayList<>();

    public Account(Long accountId, Long playerId) {
        this.accountId = accountId;
        this.playerId = playerId;
        this.balance = 0.0d;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
