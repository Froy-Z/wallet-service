package ru.ylab.services;

import ru.ylab.exceptions.DeauthorisationException;
import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.interfaces.PlayerInterface;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.util.List;
import java.util.Map;
public class PlayerService implements PlayerInterface {

    /**
     *  Поиск уже существующих игроков по логину, запись в переменную player
     * @param playerList лист для поиска существующих игроков;
     * @param login переменная от пользователя
     * @return найденный игрок или throw если игрок не найден
     * @throws InvalidCredentialException если login не найден в коллекции записей.
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
     *
     * @param credentials map-коллекция существующих учётных данных
     * @param loggingList коллекция истории действий игроков для записи
     * @param player      игрок, с которым будет вестись взаимодействие
     * @param login       логин введённый игроком
     * @param password    - пароль введённый игроком
     * @return лог выполнения
     * @throws InvalidCredentialException неверный логин или пароль
     */
    @Override
    public Logging authorisationPlayer(Map<String, String> credentials, List<Logging> loggingList,
                                       Player player, String login, String password) throws InvalidCredentialException {

        Long playerId = player.getId();
        Logging logging;
        if (credentials.containsKey(login)) {
            if (credentials.get(login).equals(password)) {
                logging = new Logging(playerId, Logging.TypeOperation.SUCCES_AUTH);
                System.out.println("Успешная авторизация. \n");
                return logging;
            } else {
                loggingList.add(new Logging(playerId, Logging.TypeOperation.FAIL_AUTH));
                throw new InvalidCredentialException("Неверный пароль, попробуйте еще раз.");
            }
        } else {
            loggingList.add(new Logging(playerId, Logging.TypeOperation.FAIL_AUTH));
            throw new InvalidCredentialException("Неверный логин, попробуйте еще раз.");
        }
    }

    /**
     * Деавторизация
     *
     * @param loggingList коллекция истории действий игроков для записи
     * @param player      игрок для деавторизации
     * @return лог выполнения
     * @throws DeauthorisationException неизвестная ошибка при деавторизации, принудительная деавторизация
     */
    @Override
    public Logging deauthorisationPlayer(List<Logging> loggingList, Player player)  {
        Logging logging;
       try {
           logging = new Logging(player.getId(), Logging.TypeOperation.SUCCES_DEAUTH);
           System.out.println("Вы успешно деавторизованы. \n");
           return logging;
       } catch (DeauthorisationException e) {
           logging = new Logging(player.getId(), Logging.TypeOperation.FAIL_DEAUTH);
           System.out.println("Произошла ошибка. Запущена принудительная деавторизация.");
           System.out.println("Вы успешно деавторизованы. \n");
           return logging;
       }
    }
}
