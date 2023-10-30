package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок авторизации игроков.
 */
public class BalanceChangeFailedException extends RuntimeException {
    public BalanceChangeFailedException(String message) {
        super(message);
    }
}
