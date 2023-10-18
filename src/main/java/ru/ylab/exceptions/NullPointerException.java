package ru.ylab.exceptions;

/**
 * Класс исключение для обозначения ошибок объектов,
 * которые являются null или не инициализированы.
 */
public class NullPointerException extends RuntimeException {
    public NullPointerException(String message) {
        super(message);
    }
}
