package ru.ylab.interfaces;

import ru.ylab.models.Player;

/**
 * Интерфейс для работы с аудитом игроков в системе.
 */
public interface LoggingInterface {
    /**
     * Метод выводит аудит игрока в консоль.
     * @param player игрок, для которого выводится аудит
     */
    void printLogs(Player player);
}
