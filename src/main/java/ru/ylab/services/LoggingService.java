package ru.ylab.services;

import ru.ylab.interfaces.ILogging;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoggingService implements ILogging {

    /**
     * Печать в консоль операций, совершенных игроком
     *
     * @param loggingList коллекция логов по игрокам
     * @param player игрок
     */
    @Override
    public void printLogs(List<Logging> loggingList, Player player) {
        Long playerId = player.getId();

        loggingList.stream()
                .filter(logging -> logging.getPlayerId().equals(playerId))
                .forEach(logging -> {
                    LocalDateTime executionTime = logging.getExecutionTime().truncatedTo(ChronoUnit.SECONDS);
                    String operationType = logging.getType().name();
                    System.out.println("- Время выполнения: " + executionTime + ", Тип операции: " + operationType);
                });
    }
}
