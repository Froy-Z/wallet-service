package ru.ylab.in.factories;

import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Фабрика для создания логов(ведения аудита игрока).
 */
public class LoggingFactory {
    /**
     * Создание лога в реальном времени
     * @param player ведение аудита игроку
     * @param type тип совершённой операции
     * @return лог для записи в БД
     */
    public Logging makeLog(Player player, Logging.TypeOperation type) {
        Long playerId = player.getId();
        Timestamp executeTime = Timestamp.valueOf(LocalDateTime.now());
        return new Logging(null, playerId, executeTime, type);
    }
}
