package ru.ylab.out.repositories;

import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.util.List;

public interface LoggingRepository {
    void save(Logging logging);
    List<Logging> findAllLogsPlayer(Player player);
}
