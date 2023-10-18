package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения недостаточности средств на счету игрока.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
