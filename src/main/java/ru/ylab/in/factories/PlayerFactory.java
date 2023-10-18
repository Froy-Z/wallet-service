package ru.ylab.in.factories;

import ru.ylab.models.Player;

/**
 * Класс фабрика для создания новых игроков.
 */
public class PlayerFactory {
    /**
     * Создания игрока при регистрации
     *
     * @param login      логин введённый пользователем
     * @param password   пароль введённый пользователем
     * @return игрок с уникальным логином и индивидуальным
     * паролем для доступа в систему и личного кошельку
     */
    public Player makePlayer(String login, String password) {
        return new Player(null, login, password);
    }
}
