package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок учётных данных игроков
 */
public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
