package ru.ylab.models;

import lombok.Data;


/**
 * Класс сущности игрока, который имеет уникальный логин. Для получения доступа также необходимо указание пароля.
 */
@Data
public class Player {
    private Long id;
    private String login;
    private String password;

    public Player(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }
    public Player(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
