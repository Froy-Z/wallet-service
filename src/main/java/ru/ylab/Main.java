package ru.ylab;

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

public class Main {
    static Player player;
    static Account account;
    static PlayerFactory playerFactory = new PlayerFactory();
    static PlayerService playerService = new PlayerService();
    static AccountFactory accountFactory = new AccountFactory();
    static AccountService accountService = new AccountService();
    static LoggingService loggingService = new LoggingService();
    static TransactionService transactionService = new TransactionService();
    static final Map<String, String> CREDENTIALS = new HashMap<>();
    static final List<Player> PLAYER_LIST = new ArrayList<>();
    static final List<Account> ACCOUNT_LIST = new ArrayList<>();
    static final List<Transaction> TRANSACTION_LIST = new ArrayList<>();
    static final List<Logging> LOGGING_LIST = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("\n Добро пожаловать в онлайн-казино  \"Пустой карман\" \n");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            startCasino(bufferedReader);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /*
     * Главное меню
     */
    private static void startCasino(BufferedReader bufferedReader) throws IOException {
        System.out.println("--------- Добро пожаловать -------- \n");
        String inputStr;
        while (true) {
            System.out.println("Для входа в систему введите \"Вход\", для выхода \"Выход\"");
            inputStr = stringUserInput(bufferedReader);
            switch (inputStr) {
                case "Вход" -> registrationAndAuthorisation(bufferedReader);
                case "Выход" -> {
                    System.out.println("До свидания! Приносите ещё к Нам Ваши денежки!");
                    return;
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
            }
        }
    }

    /*
     * Меню регистрации и авторизации
     */
    private static void registrationAndAuthorisation(BufferedReader bufferedReader) {
        while (true) {
            System.out.println("--------- Регистрация и Авторизация -------- \n");
            System.out.println("Введите \"Регистрация\", \"Авторизация\" или вернуться к прошлому шагу \"Выход\"");
            String inputStr = stringUserInput(bufferedReader);
            switch (inputStr) {
                case "Регистрация" -> registration(bufferedReader);
                case "Авторизация" -> authorisation(bufferedReader);
                case "Выход" -> {
                    return;
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
            }
        }
    }

    /*
     * Первичное взаимодействие с игроком
     */
    private static void registration(BufferedReader bufferedReader) {
        while (true) {
            System.out.println("--------- Регистрация -------- \n");
            System.out.println("Для возврата к прошлому шагу введите \"Выход\". \n");
            System.out.println("Придумайте, пожалуйста логин: ");
            String login = stringUserInput(bufferedReader);
            System.out.println();
            if (login.equals("Выход")) {
                return;
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
            player = playerFactory.makePlayer(PLAYER_LIST, login, password);
            PLAYER_LIST.add(player);
            System.out.println("Успешная регистрация! \n");
            return;
        }
    }

    /*
     * Авторизация игрока в системе
     */
    private static void authorisation(BufferedReader bufferedReader) {
        String login;
        String password;

        while (true) {
            /*
             * Процесс авторизации
             */
            System.out.println("\n-------- Авторизация -------- \n");
            System.out.println("Для возврата к прошлому шагу введите \"Выход\"");
            System.out.println("Введите, пожалуйста логин: ");
            login = stringUserInput(bufferedReader);
            System.out.println();
            if (login.equals("Выход")) {
                return;
            }
            System.out.print("Введите, пожалуйста пароль: ");
            password = stringUserInput(bufferedReader);
            System.out.println();
            /*
             * Процесс проверки логина и пароля
             */
            try {
                /*
                 * Поиск уже существующих игроков по логину, запись в переменную player
                 */
                player = playerService.searchPlayer(PLAYER_LIST, login);
                LOGGING_LIST.add(playerService.authorisationPlayer(CREDENTIALS, LOGGING_LIST, player, login, password));
                personalArea(bufferedReader); // вход в личный кабинет игрока
                return;
            } catch (InvalidCredentialException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /*
     * Личный кабинет игрока
     */
    private static void personalArea(BufferedReader bufferedReader) {
        System.out.printf("\n-------- Личный профиль %s -------- ", player.getLogin());
        System.out.printf("\nЗдравствуйте %s! \n", player.getLogin());
        account = accountService.searchAccountPlayer(ACCOUNT_LIST, player.getId());

        // Если у игрока нет кошелька
        if (account == null) {
            account = createdFirstAccount(bufferedReader);
        }

        if(account != null) {
            playerManipulation(bufferedReader);
        }
    }

    /*
     * Основные манипуляции игрока в системе
     */
    private static void playerManipulation(BufferedReader bufferedReader) {
        double inputDouble;
        String input;
        while (true) {
            System.out.println("\nВам доступны следующие функции: Просмотр баланса, Пополнение, Списание, Аудит игрока, История операций, Деавторизация");
            input = stringUserInput(bufferedReader);
            switch (input) {
                case "Просмотр баланса" -> {
                    System.out.println("-------- Ваш баланс -------- \n");
                    accountService.getAccountBalance(account);
                }
                case "Пополнение" -> {
                    System.out.println("-------- Пополнение -------- \n");
                    System.out.println("На сколько желаете пополнить счёт?");
                    inputDouble = doubleUserInput(bufferedReader);
                    try {
                        accountService.performCreditTransaction(ACCOUNT_LIST, LOGGING_LIST, TRANSACTION_LIST, player, account.getAccountId(), inputDouble);
                    } catch (InsufficientFundsException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "Списание" -> {
                    while (true) {
                        System.out.println("-------- Списание -------- \n");
                        System.out.println("Cколько желаете снять со счёта?");
                        inputDouble = doubleUserInput(bufferedReader);
                        try {
                            accountService.performDebitTransaction(ACCOUNT_LIST, LOGGING_LIST, TRANSACTION_LIST, player, account.getAccountId(), inputDouble);
                            break;
                        } catch (InsufficientFundsException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                /*
                 * Аудит игрока, выводит в консоль время и тип всех операций игрока
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
                    LOGGING_LIST.add(playerService.deauthorisationPlayer(LOGGING_LIST, player));
                    return;
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
            }
        }
    }

    /*
     * Обязательное создание кошелька
     */
    private static Account createdFirstAccount(BufferedReader bufferedReader) {
        String input;

        while (true) {
            System.out.println("\n-------- Создание кошелька -------- \n");
            System.out.println("У вас нет активных счетов, создадим? Да/Нет");
            System.out.println("При выборе \"Нет\", будет произведена деавторизация.");
            input = stringUserInput(bufferedReader);

            switch (input) {
                /*
                 * Создание первого кошелька
                 */
                case "Да" -> {
                    ACCOUNT_LIST.add(accountFactory.makeAccount(ACCOUNT_LIST, player));
                    account = accountService.searchAccountPlayer(ACCOUNT_LIST, player.getId());
                    System.out.printf("Успешно создан кошелёк! ID вашего кошелька № %d.", account.getAccountId());
                    System.out.printf("\n-------- Личный профиль %s -------- \n", player.getLogin());
                    return account;
                }
                /*
                 * Деавторизация игрока по умолчанию
                 */
                case "Нет" -> {
                    LOGGING_LIST.add(playerService.deauthorisationPlayer(LOGGING_LIST, player));
                    return null;
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз.");
            }
        }
    }

    private static void handleIOException(IOException e) {
        System.out.println("\nПроизошла ошибка ввода-вывода: " + e.getMessage());
    }
}
