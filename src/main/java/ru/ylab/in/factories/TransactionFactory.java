package ru.ylab.in.factories;

import ru.ylab.models.Account;
import ru.ylab.models.Transaction;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcTransaction;
import ru.ylab.out.repositories.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.UUID.randomUUID;

/**
 * Класс фабрика для создания транзакций.
 */
public class TransactionFactory {

    /**
     * Создать транзакцию и сохранить в общую коллекцию транзакций игрока
     * @param account коллекция транзакций игрока
     * @param amount  сумма к исполнению
     * @param type    тип транзакций для отражения денежных потоков на счетах игроков
     * @return уникальная транзакция в рамках кошелька конкретного игрока.
     * @see ru.ylab.out.jdbc.JdbcTransaction уникальность контролируется на уровне базы данных.
     */
    public Transaction makeTransaction(Account account, BigDecimal amount, Transaction.TypeOperation type) {
        Long playerId = account.getPlayerId();
        Long accountId = account.getId();
        String uuid = getUniqueUUID(playerId, accountId);

        Timestamp executeTime = Timestamp.valueOf(LocalDateTime.now());
        return new Transaction(playerId, accountId, uuid, executeTime, amount, type);
    }

    /**
     * Метод для генерации уникального идентификатора UUID.
     * @param playerId id игрока
     * @param accountId id кошелька игрока
     * @return уникальный идентификатор для транзакции.
     */
    private String getUniqueUUID(Long playerId, Long accountId) {
        String uuid;
        boolean valid;

        while (true) {
            uuid = String.valueOf(randomUUID());
            valid = checkDublicateUUID(uuid, playerId, accountId);
            if(valid) {
                return uuid;
            }
        }
    }

    /**
     * Проверка на совпадение транзакций.
     * @param uuid идентификатор для проверки
     * @param playerId id игрока
     * @param accountId id кошелька игрока
     * @return булево значение результата проверки.
     */
    private boolean checkDublicateUUID(String uuid, Long playerId, Long accountId) {
        List<String> listUUID;

        try (Connection connection1 = DatabaseConnector.getConnection()) {
            TransactionRepository transactionRepository = new JdbcTransaction(connection1);
            listUUID = transactionRepository.findAllUUIDPlayer(playerId, accountId);
            return listUUID.stream()
                    .noneMatch(s -> s.equals(uuid));
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}