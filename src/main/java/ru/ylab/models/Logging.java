package ru.ylab.models;

import java.time.LocalDateTime;

public class Logging {

    private final Long playerId;
    private final LocalDateTime executionTime = LocalDateTime.now();
    private final TypeOperation type;

    public Logging(Long playerId , TypeOperation type) {
        this.playerId = playerId;
        this.type = type;
    }
    public enum TypeOperation {
        SUCCES_AUTH,
        FAIL_AUTH,
        SUCCES_CREATION_ACCOUNT,
        FAIL_CREATION_ACCOUNT,
        SUCCES_DEBIT,
        FAIL_DEBIT,
        SUCCES_CREDIT,
        FAIL_CREDIT,
        SUCCES_DEAUTH,
        FAIL_DEAUTH

    }

    public Long getPlayerId() {
        return playerId;
    }
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public TypeOperation getType() {
        return type;
    }

}
