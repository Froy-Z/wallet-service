package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок авторизации игроков.
 */
public class TransactionSaveException extends RuntimeException {
    public TransactionSaveException(String message) {
        super(message);
    }
}
