package ru.ylab;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.ylab.exceptions.DeauthorisationException;
import ru.ylab.exceptions.InsufficientFundsException;
import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.in.factories.AccountFactory;
import ru.ylab.in.factories.PlayerFactory;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.models.Transaction;
import ru.ylab.services.AccountService;
import ru.ylab.services.LoggingService;
import ru.ylab.services.PlayerService;
import ru.ylab.services.TransactionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.ylab.in.ConsoleInputHandler.doubleUserInput;
import static ru.ylab.in.ConsoleInputHandler.stringUserInput;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Main {

    static Player player;
    static Account account;
    static PlayerFactory playerFactory = new PlayerFactory();
    static PlayerService playerService = new PlayerService();
    static AccountFactory accountFactory = new AccountFactory();
    static AccountService accountService = new AccountService();
    static LoggingService loggingService = new LoggingService();
    static TransactionService transactionService = new TransactionService();
    static Map<String, String> CREDENTIALS = new HashMap<>();
    static List<Player> PLAYER_LIST = new ArrayList<>();
    static List<Account> ACCOUNT_LIST = new ArrayList<>();
    static List<Transaction> TRANSACTION_LIST = new ArrayList<>();
    static List<Logging> LOGGING_LIST = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("\n Добро пожаловать в онлайн-казино  \"Пустой карман\" \n");


        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {

            Main:
            while (true) {

                System.out.println("--------- Добро пожаловать -------- \n");

                /*
                  Реализация входа в сервис
                 */
                Entry:
                while (true) {

                    System.out.println("Для входа в систему введите \"Вход\", для выхода  \"Выход\"");

                    String inputStr = stringUserInput(bufferedReader);
                    double inputDbl;

                    switch (inputStr) {

                        /*
                         * вход в меню Регистрации и Авторизации
                         */
                        case "Вход" -> {

                            while (true) {

                                System.out.println("--------- Регистрация и Авторизация -------- \n");

                                System.out.println("Введите \"Регистрация\", \"Авторизация\" или вернуться к прошлому шагу \"Выход\"");

                                inputStr = stringUserInput(bufferedReader);

                                switch (inputStr) {
                                    /*
                                     * Первичное взаимодействие с игроком
                                     */
                                    case "Регистрация" -> {

                                        while (true) {

                                            System.out.println("--------- Регистрация -------- \n");

                                            System.out.println("Для возврата к прошлому шагу введите \"Выход\". \n");

                                            System.out.println("Придумайте, пожалуйста логин: ");
                                            String login = stringUserInput(bufferedReader);
                                            System.out.println();


                                            if (login.equals("Выход")) {
                                                break;
                                            }

                                            System.out.println("Придумайте, пожалуйста пароль: ");
                                            String password = stringUserInput(bufferedReader);
                                            System.out.println();

                                            try {
                                                playerFactory.addCredentials(CREDENTIALS, login, password);
                                            } catch (InvalidCredentialException e) {
                                                System.out.println(e.getMessage());
                                                continue;
                                            }


                                            playerFactory.makePlayer(PLAYER_LIST, login, password);

                                            System.out.println("Успешная регистрация! \n");
                                            break;
                                        }
                                    }
                                    /*
                                     * Основная логика взаимодействия с игроками в данном блоке
                                     */
                                    case "Авторизация" -> {

                                        Authorisation:
                                        while (true) {

                                            /*
                                              Процесс авторизации
                                             */

                                            System.out.println("\n-------- Авторизация -------- \n");

                                            System.out.println("Для возврата к прошлому шагу введите \"Выход\"");

                                            System.out.println("Введите, пожалуйста логин: ");
                                            String login = stringUserInput(bufferedReader);
                                            System.out.println();

                                            if (login.equals("Выход")) {
                                                break;
                                            }

                                            System.out.print("Введите, пожалуйста пароль: ");
                                            String password = stringUserInput(bufferedReader);
                                            System.out.println();

                                            /*
                                               Процесс проверки логина и пароля
                                             */
                                            try {
                                                /*
                                                   Поиск уже существующих игроков по логину, запись в переменную player
                                                 */
                                                player = playerService.searchPlayer(PLAYER_LIST, login);

                                                /*
                                                  Авторизация
                                                 */
                                                playerService.authorisationPlayer(CREDENTIALS, LOGGING_LIST, player, login, password);

                                            } catch (InvalidCredentialException e) {
                                                System.out.println(e.getMessage());
                                                continue;
                                            }

                                            /*
                                            Вход в профиль игрока
                                             */
                                            while (true) {

                                                System.out.printf("\n-------- Личный профиль %s -------- \n", player.getLogin());

                                                System.out.printf("Здравствуйте %s! \n", player.getLogin());

                                                account = accountService.searchAccountPlayer(ACCOUNT_LIST, player.getId());


                                                // Если у игрока нет кошелька
                                                if (account == null) {


                                                    createdAccount:
                                                    while (true) {

                                                        System.out.println("У вас нет активных счетов, создадим? Да/Нет");
                                                        System.out.println("При выборе \"Нет\", будет произведена деавторизация.");

                                                        inputStr = stringUserInput(bufferedReader);

                                                        switch (inputStr) {
                                                            /*
                                                             * Создание первого кошелька
                                                             */
                                                            case "Да" -> {
                                                                accountFactory.makeAccount(ACCOUNT_LIST, player);

                                                                account = accountService.searchAccountPlayer(ACCOUNT_LIST, player.getId());

                                                                System.out.println("\n-------- Создание кошелька -------- \n");

                                                                System.out.printf("Успешно создан кошелёк! ID вашего кошелька № %d. \n", account.getAccountId());
                                                                break createdAccount;
                                                            }
                                                            /*
                                                             * Деавторизация игрока по умолчанию
                                                             */
                                                            case "Нет" -> {
                                                                try {
                                                                    playerService.deauthorisationPlayer(LOGGING_LIST, player);
                                                                } catch (DeauthorisationException e) {
                                                                    System.out.println(e.getMessage());
                                                                    break createdAccount;
                                                                }
                                                            }
                                                            default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз.");
                                                        }
                                                    }
                                                }

                                                /*
                                                 * Взаимодействие с игроком в профиле
                                                 */
                                                while (true) {

                                                    System.out.printf("-------- Личный профиль %s -------- \n", player.getLogin());

                                                    System.out.println("\nВам доступны следующие функции: Просмотр баланса, Пополнение, Списание, Аудит игрока, История операций, Деавторизация");

                                                    inputStr = stringUserInput(bufferedReader);

                                                    switch (inputStr) {
                                                        case "Просмотр баланса" -> {
                                                            System.out.println("-------- Ваш баланс -------- \n");
                                                            accountService.getAccountBalance(account);
                                                        }
                                                        /*
                                                         * Пополнение кошелька
                                                         */
                                                        case "Пополнение" -> {
                                                            System.out.println("-------- Пополнение -------- \n");

                                                            System.out.println("На сколько желаете пополнить счёт?");
                                                            inputDbl = doubleUserInput(bufferedReader);

                                                            try {

                                                                accountService.performCreditTransaction(ACCOUNT_LIST, LOGGING_LIST, TRANSACTION_LIST, player,account.getAccountId(), inputDbl);

                                                            } catch (InsufficientFundsException e) {
                                                                System.out.println(e.getMessage());
                                                            }
                                                        }
                                                        /*
                                                         * Списание денежных средств с кошелька
                                                         */
                                                        case "Списание" -> {

                                                            while (true) {

                                                                System.out.println("-------- Списание -------- \n");

                                                                System.out.println("Cколько желаете снять со счёта?");
                                                                inputDbl = doubleUserInput(bufferedReader);

                                                                try {

                                                                    accountService.performDebitTransaction(ACCOUNT_LIST, LOGGING_LIST, TRANSACTION_LIST, player, account.getAccountId(), inputDbl);

                                                                    break;

                                                                } catch (InsufficientFundsException e) {

                                                                    System.out.println(e.getMessage());
                                                                }
                                                            }
                                                        }

                                                        /*
                                                         * Аудит игрока, выводит на консоль время и тип всех операций игрока
                                                         */
                                                        case "Аудит игрока" -> {
                                                            System.out.println("-------- Аудит игрока -------- \n");

                                                            System.out.println("Ваш список действий игрока:");
                                                            loggingService.printLogs(LOGGING_LIST, player);
                                                        }
                                                        /*
                                                         * История денежных потоков игрока
                                                         */
                                                        case "История операций" -> {

                                                            System.out.println("Ваш список транзакций кошелька:");
                                                            transactionService.printTransaction(TRANSACTION_LIST, player);

                                                        }
                                                        /*
                                                         * Деавторизует игрока
                                                         */
                                                        case "Деавторизация" -> {
                                                            try {

                                                                playerService.deauthorisationPlayer(LOGGING_LIST, player);
                                                                break Authorisation;

                                                            } catch (DeauthorisationException e) {

                                                                System.out.println(e.getMessage());
                                                                break Authorisation;
                                                            }
                                                        }
                                                        default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
                                                    }
                                                }
                                            }

                                        }
                                    }
                                    /*
                                     * Реализация выхода на шаг назад
                                     */
                                    case "Выход" -> {

                                        break Entry;
                                    }
                                    default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");

                                }
                            }
                        }
                        /*
                         * Выход из программы
                         */
                        case "Выход" -> {
                            System.out.println("До свидания! Приносите ещё к Нам Ваши денежки!");
                            break Main;
                        }
                        default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("\nПроизошла ошибка ввода-вывода: " + e.getMessage());
        }
    }
}

