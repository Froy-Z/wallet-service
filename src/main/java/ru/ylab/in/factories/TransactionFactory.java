package ru.ylab.in.factories;

import ru.ylab.models.Account;
import ru.ylab.models.Transaction;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Класс фабрика для создания транзакций.
 */
public class TransactionFactory {

    /**
     * Создать транзакцию и сохранить в общую коллекцию транзакций игрока
     * @param account коллекция транзакций игрока
     * @param amount  сумма к исполнению
     * @param type    тип транзакций для отражения денежных потоков на счетах игроков
     * @return новая транзакция, без уникального номера ID и UUID
     * @see ru.ylab.out.jdbc.JdbcTransaction уникальность контролируется на уровне базы данных.
     */
    public Transaction makeTransaction(Account account, double amount, Transaction.TypeOperation type) {
        Long playerId = account.getPlayerId();
        Long accountId = account.getId();
        Timestamp executeTime = Timestamp.valueOf(LocalDateTime.now());
        return new Transaction(null, playerId, accountId, null, executeTime, amount, type);
    }
}