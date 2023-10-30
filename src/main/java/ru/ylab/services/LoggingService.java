package ru.ylab.services;

import ru.ylab.in.factories.LoggingFactory;
import ru.ylab.interfaces.LoggingInterface;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcLogging;
import ru.ylab.out.repositories.LoggingRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class LoggingService implements LoggingInterface {
    private final LoggingFactory loggingFactory = new LoggingFactory();
    LoggingRepository loggingRepository;

    /**
     * Печать в консоль всех операций, совершенных игроком
     * @param player игрок
     */
    @Override
    public void printLogs(Player player) {
        Logging logging;

        try (Connection connection = DatabaseConnector.getConnection()) {
            loggingRepository = new JdbcLogging(connection); // соединение с БД аудита
            List<Logging> loggingList = loggingRepository.findAllLogsPlayer(player); // выгрузка аудита в список
            loggingList.forEach(log -> {
                        Timestamp executionTime = log.getExecutionTime();
                        String operationType = log.getType().name();
                        System.out.println("- Время выполнения: " + executionTime + ", Тип операции: " + operationType);
                    }); // вывод в консоль аудита игрока
            logging = loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_VIEW_AUDIT);// логирование
            loggingRepository.save(logging); // успешный просмотр аудита
        } catch (SQLException e) {
            logging = loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_VIEW_AUDIT); // логирование
            loggingRepository.save(logging); // неудачная попытка просмотра аудита
            System.out.println("Произошла ошибка вывода аудита игрока.");
        }
    }
}
