package ru.ylab.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Класс представляет собой лицевой счёт(кошелёк) игрока.
 * Каждый кошелёк связан с определённым игроком и содержит информацию о балансе
 */
@Data
public class Account {
    private Long id; // идентификатор кошелька игрока
    private Long playerId; // идентификатор игрока, которому принадлежит кошелёк
    private BigDecimal balance; // баланс игрока по счёту

    public Account(Long id, Long playerId, BigDecimal balance) {
        this.id = id;
        this.playerId = playerId;
        this.balance = balance;
    }

    public Account(Long playerId, BigDecimal balance) {
        this.playerId = playerId;
        this.balance = balance;
    }
}