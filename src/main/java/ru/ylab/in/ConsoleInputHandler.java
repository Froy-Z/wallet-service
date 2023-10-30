package ru.ylab.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Класс взаимодействия игроков посредством консольного ввода.
 */
public class ConsoleInputHandler {
    /**
     * Консольное выполнение строковых запросов пользователя
     *
     * @param bufferedReader input-данных от пользователя
     * @return корректная строка от пользователя
     */
    public static String stringUserInput(BufferedReader bufferedReader) {
        while (true) {
            try {
                System.out.println("Ввод: ");
                String input = bufferedReader.readLine();
                if (!input.trim().isEmpty()) {
                    return input;
                } else {
                    System.out.println("Ошибка: введённые данные не могут быть пустыми!");
                }
            } catch (IOException e) {
                System.out.println("Ошибка ввода: " + e.getMessage());
            }
        }
    }

    /**
     * Консольное выполнение числовых с плавающей точкой запросов пользователя
     *
     * @param bufferedReader input-данных от пользователя
     * @return корректное число для работы с балансом игрока
     */

    public static BigDecimal doubleUserInput(BufferedReader bufferedReader) {
        while (true) {
            try {
                System.out.println("Ввод: ");
                String userInput = bufferedReader.readLine().trim();
                if (!userInput.isEmpty()) {
                    return new BigDecimal(userInput);
                } else {
                    System.out.println("Ошибка ввода. Введённые данные не могут быть пустыми.");
                }
            } catch (NumberFormatException | IOException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите корректное число.");
            }
        }
    }
}
