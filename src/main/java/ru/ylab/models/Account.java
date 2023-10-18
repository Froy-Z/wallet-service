package ru.ylab.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс представляет собой лицевой счёт(кошелёк) игрока.
 * Каждый кошелёк связан с определённым игроком и содержит информацию о балансе
 */
@Data
@AllArgsConstructor
public class Account {
    private Long id; // идентификатор кошелька игрока
    private Long playerId; // идентификатор игрока, которому принадлежит кошелёк
    private double balance; // баланс игрока по счёту
}