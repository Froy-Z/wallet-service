package ru.ylab.models;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Класс сущности транзакции. Транзакция имеет порядковый номер, ID игрока кому принадлежит транзакция и ID счёта (кошелька)
 * по которому она проведена. Имеет уникальный UUID идентификатор для исключения дублирования транзакций, время исполнения операции,
 * сумма к исполнению и тип операции, выраженный в Enum.
 */
@Data
public class Transaction {
    private Long id;
    private Long playerId;
    private Long accountId;
    private String transactionId;
    private Timestamp executeTime;
    private BigDecimal amount;
    private TypeOperation type;

    public Transaction(Long playerId, Long accountId, String transactionId, Timestamp executeTime, BigDecimal amount, TypeOperation type) {
        this.playerId = playerId;
        this.accountId = accountId;
        this.transactionId = transactionId;
        this.executeTime = executeTime;
        this.amount = amount;
        this.type = type;
    }

    public enum TypeOperation {
        DEBIT,
        CREDIT
    }
}
