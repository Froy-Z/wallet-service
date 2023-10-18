package ru.ylab.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;

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
                if (input != null && !input.trim().isEmpty()) {
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

    public static double doubleUserInput(BufferedReader bufferedReader) {
        Double input = 0.0;
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.println("Ввод: ");
                input = Double.parseDouble(bufferedReader.readLine().trim());
                validInput = true;
                if (!input.isNaN()) {
                    return input;
                } else {
                    System.out.println("Ошибка: введённые данные не могут быть пустыми!");
                }
            } catch (NumberFormatException | IOException e) {
                System.out.println("Ошибка ввода. Пожалуйста, введите корректное число");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedInput = decimalFormat.format(input);
        return Double.parseDouble(formattedInput);
    }

}
