package ru.ylab.interfaces;

import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

/**
 * Интерфейс авторизации и деавторизации игроков
 */
public interface PlayerInterface {
    /**
     * Авторизация игрока в системе.
     * @param player игрок для сравнения логина и пароля;
     * @param login логин, введённый игроком
     * @param password пароль, введённый игроком
     * @return лог об успешной или неуспешной авторизации
     * @throws InvalidCredentialException исключение ошибочного логина или пароля
     */
    Logging authorisationPlayer(Player player, String login, String password) throws InvalidCredentialException;

    /**
     * Деавторизация игрока - выход из системы.
     * @param player деавторизующийся игрок
     * @return лог о корректной деавторизации или принудительной
     */
    Logging deauthorisationPlayer(Player player);
}
