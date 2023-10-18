package ru.ylab.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Класс сущности транзакции. Транзакция имеет порядковый номер, ID игрока кому принадлежит транзакция и ID счёта (кошелька)
 * по которому она проведена. Имеет уникальный UUID идентификатор для исключения дублирования транзакций, время исполнения операции,
 * сумма к исполнению и тип операции, выраженный в Enum.
 */
@Data
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Long playerId;
    private Long accountId;
    private String transactionId;
    private Timestamp executeTime;
    private double amount;
    private TypeOperation type;
    public enum TypeOperation {
        SUCCES_DEBIT,
        FAIL_DEBIT,
        SUCCES_CREDIT,
        FAIL_CREDIT
    }
}
