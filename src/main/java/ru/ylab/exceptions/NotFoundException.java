package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок авторизации игроков.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
