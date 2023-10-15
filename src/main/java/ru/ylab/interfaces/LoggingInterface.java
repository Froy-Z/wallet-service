package ru.ylab.interfaces;

import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.util.List;

public interface LoggingInterface {
    void printLogs(List<Logging> loggingList, Player player);
}
