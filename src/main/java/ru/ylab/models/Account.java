package ru.ylab.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс представляет собой лицевой счёт(кошелёк) игрока
 * Каждый кошелёк связан с определённым игроком и содержит информацию о балансе и истории транзакций
 */
public class Account {
    private final Long accountId; // идентификатор кошелька игрока
    private final Long playerId; // идентификатор игрока, которому принадлежит кошелёк
    private double balance; // баланс игрока по счёту
    private final List<Transaction> transactionList = new ArrayList<>(); // список транзакций игрока

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
