package ru.ylab.models;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Класс сущности игрока, который имеет уникальный логин. Для получения доступа также необходимо указание пароля.
 */
@Data
@AllArgsConstructor
public class Player {
    private Long id;
    private String login;
    private String password;
}
