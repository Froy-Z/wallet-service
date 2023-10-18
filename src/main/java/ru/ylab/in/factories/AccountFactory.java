package ru.ylab.in.factories;

import ru.ylab.models.Account;
import ru.ylab.models.Player;


/**
 * Класс фабрика для создания объектов типа Account (кошелёк).
 */
public class AccountFactory {

    /**
     * Создание кошелька игроку
     * @param player игрок для присвоения кошелька
     * @return объект Account, без уникального ID, присваивается значения null;
     * @see ru.ylab.out.jdbc.JdbcAccount присвоение уникального ID контролируется на уровне базы данных.
     */
    public Account makeAccount(Player player) {
        Long playerId = player.getId();
        return new Account(null, playerId, 0.00);
    }
}
