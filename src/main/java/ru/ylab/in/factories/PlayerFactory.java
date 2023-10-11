package ru.ylab.in.factories;

import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.models.Player;

import java.util.List;
import java.util.Map;

public class PlayerFactory {
    /**
     * Создания игрока при регистрации
     *
     * @param playerList коллекция игроков для сравнения и добавления нового игрока
     * @param login      логин ведённый пользователем
     * @param password   - пароль ведённый пользователем
     */
    public void makePlayer(List<Player> playerList, String login, String password) {
        Long playerId = generatePlayerId(playerList);
        Player player = new Player(playerId, login, password);
        playerList.add(player);
    }

    /**
     * Добавление логина и пароля в map-коллекцию учётных данных
     *
     * @param credentials map-коллекция для учётных данных игроков
     * @param login       логин ведённый пользователем
     * @param password    пароль ведённый пользователем
     * @throws InvalidCredentialException пробрасывание ошибки, если такой логин уже существует
     */
    public void addCredentials(Map<String, String> credentials, String login, String password) throws InvalidCredentialException {

        if (!credentials.containsKey(login)) {
            credentials.put(login, password);
        } else {
            throw new InvalidCredentialException(
                    String.format(
                            "Логин %s уже занят. \n",
                            login
                    )
            );
        }
    }

    /**
     * Генерация ID игроков
     *
     * @param playerList коллекция игроков
     * @return свободный ID для игрока
     */
    private Long generatePlayerId(List<Player> playerList) {
        return playerList.stream()
                .map(Player::getId)
                .max(Long::compare)
                .map(maxId -> maxId + 1)
                .orElse(1L);
    }
}
