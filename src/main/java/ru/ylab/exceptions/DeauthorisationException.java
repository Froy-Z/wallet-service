package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок авторизации игроков.
 */
public class DeauthorisationException extends RuntimeException {
    public DeauthorisationException(String message) {
        super(message);
    }
}
