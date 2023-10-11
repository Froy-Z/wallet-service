package ru.ylab.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final Long playerId;
    private final String transactionId;
    private final LocalDateTime executeTime = LocalDateTime.now();
    private final double amount;
    private final Logging.TypeOperation type;

    // должна быть проверена транзакция на уникальность
    public Transaction(Long playerId, double amount, Logging.TypeOperation type) {
        this.playerId = playerId;
        this.transactionId = UUID.randomUUID().toString();
        this.amount = amount;
        this.type = type;
    }


    public String getTransactionId() {
        return transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public Long getPlayerId() {
        return playerId;
    }
    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public Logging.TypeOperation getType() {
        return type;
    }
}
