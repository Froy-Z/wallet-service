package ru.ylab.services;

import ru.ylab.exceptions.DeauthorisationException;
import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.in.factories.LoggingFactory;
import ru.ylab.interfaces.PlayerInterface;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.util.List;

public class PlayerService implements PlayerInterface {

    LoggingFactory loggingFactory;

    /**
     * Поиск уже существующих игроков по логину, запись в переменную player
     *
     * @param playerList лист для поиска существующих игроков;
     * @param login      переменная от пользователя
     * @return найденный игрок или throw если игрок не найден
     * @throws InvalidCredentialException пробрасывание исключения
     */
    public Player searchPlayer(List<Player> playerList, String login) throws InvalidCredentialException {
        return playerList.stream()
                .filter(player -> player.getLogin().equals(login))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidCredentialException(
                                String.format(
                                        "Пользователь с логином: %s не найден. Попробуйте ещё раз.",
                                        login
                                )
                        )
                );
    }

    /**
     * Авторизация
     * @param player      игрок для авторизации
     * @param login       логин введённый игроком
     * @param password    пароль введённый игроком
     * @return лог выполнения
     * @throws InvalidCredentialException неверный логин или пароль
     */
    @Override
    public Logging authorisationPlayer(Player player, String login, String password) throws InvalidCredentialException {

        String loginVerification = player.getLogin();
        String passwordVerification = player.getPassword();
        Logging logging;

        if(login.equals(loginVerification)) { // если логин верен

            if(password.equals(passwordVerification)) { // если пароль верен
                logging = loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_AUTH);
                System.out.println("Успешная авторизация. \n");
                return logging;
            } else { // пароль не верен
                throw new InvalidCredentialException("Неверный пароль, попробуйте еще раз.");
            }
        } else { // логин не верен
            throw new InvalidCredentialException("Неверный логин, попробуйте еще раз.");
        }
    }

    /**
     * Деавторизация игрока
     * @param player игрок для деавторизации
     * @return лог выполнения
     * @throws DeauthorisationException неизвестная ошибка при деавторизации, принудительная деавторизация
     */
    @Override
    public Logging deauthorisationPlayer(Player player) {
        Logging logging;
        try {
            logging = loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_DEAUTH);
            System.out.println("Вы успешно деавторизованы. \n");
            return logging;
        } catch (DeauthorisationException e) {
            logging = loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_DEAUTH);
            System.out.println("Произошла ошибка. Запущена принудительная деавторизация.");
            System.out.println("Вы успешно деавторизованы. \n");
            return logging;
        }
    }


    /**
     * Проверка логина на незанятость
     * @param playerList выгруженный список игроков для сравнения логинов
     * @param login логин ведённый пользователем
     * @throws InvalidCredentialException пробрасывание ошибки, если такой логин уже существует (занят)
     */
    public void checkLogin(List<Player> playerList, String login) throws InvalidCredentialException {
        boolean loginExists = playerList.stream()
                .anyMatch(player -> player.getLogin().equals(login));

        if (loginExists) {
            throw new InvalidCredentialException(
                    String.format("Логин %s уже занят. \n", login)
            );
        }
    }
}
