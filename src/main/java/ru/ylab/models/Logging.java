package ru.ylab.models;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Класс сущности аудита, которая имеет свой порядковый номер ID,
 * внешний ключ ID игрока, время исполнения операции и тип операции в enum
 */
@Data
public class Logging {
    private Long id;
    private Long playerId;
    private Timestamp executionTime;
    private TypeOperation type;

    public Logging(Long playerId, Timestamp executionTime, TypeOperation type) {
        this.playerId = playerId;
        this.executionTime = executionTime;
        this.type = type;
    }

    public enum TypeOperation {
        SUCCES_REGISTRATION,
        SUCCES_AUTH,
        FAIL_AUTH,
        SUCCES_CREATION_ACCOUNT,
        FAIL_CREATION_ACCOUNT,
        SUCCES_DEBIT,
        FAIL_DEBIT,
        SUCCES_CREDIT,
        FAIL_CREDIT,
        SUCCES_DEAUTH,
        FAIL_DEAUTH,
        SUCCES_VIEW_AUDIT,
        FAIL_VIEW_AUDIT
    }
}
