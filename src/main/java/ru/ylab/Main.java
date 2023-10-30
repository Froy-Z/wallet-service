package ru.ylab;

import ru.ylab.exceptions.*;
import ru.ylab.exceptions.NullPointerException;
import ru.ylab.in.factories.AccountFactory;
import ru.ylab.in.factories.LoggingFactory;
import ru.ylab.in.factories.PlayerFactory;
import ru.ylab.models.Account;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;
import ru.ylab.out.DatabaseConnector;
import ru.ylab.out.jdbc.JdbcAccount;
import ru.ylab.out.jdbc.JdbcLogging;
import ru.ylab.out.jdbc.JdbcPlayer;
import ru.ylab.out.repositories.AccountRepository;
import ru.ylab.out.repositories.LoggingRepository;
import ru.ylab.out.repositories.PlayerRepository;
import ru.ylab.services.AccountService;
import ru.ylab.services.LoggingService;
import ru.ylab.services.PlayerService;
import ru.ylab.services.TransactionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static ru.ylab.in.ConsoleInputHandler.doubleUserInput;
import static ru.ylab.in.ConsoleInputHandler.stringUserInput;

public class Main {
    private static Player player = null;
    private static Account account;
    private static Logging logging;
    private static PlayerRepository playerRepository;
    private static AccountRepository accountRepository;
    private static LoggingRepository loggingRepository;
    private static List<Player> PLAYER_LIST = new ArrayList<>();
    private static final PlayerFactory playerFactory = new PlayerFactory();
    private static final AccountFactory accountFactory = new AccountFactory();
    private static final LoggingFactory loggingFactory = new LoggingFactory();
    private static final PlayerService playerService = new PlayerService();
    private static final AccountService accountService = new AccountService();
    private static final LoggingService loggingService = new LoggingService();
    private static final TransactionService transactionService = new TransactionService();

    public static void main(String[] args) {

        try {
            DatabaseConnector.addDatabaseChanges(); // миграция данных в БД
            sleep(2500);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n Добро пожаловать в онлайн-казино  \"Пустой карман\" \n");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            startCasino(bufferedReader);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * @param bufferedReader буфер для обработки ввода пользователя
     * @throws IOException пробрасывание ошибки ввода-вывода
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
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз.\n");
            }
        }
    }

    /**
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

    /**
     * Регистрация нового игрока в системе с сохранением в БД
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

            try (Connection connection = DatabaseConnector.getConnection()) {
                playerRepository = new JdbcPlayer(connection); // соединение с БД для сохранения игрока
                loggingRepository = new JdbcLogging(connection); // соединение с БД для ведения аудита
                PLAYER_LIST = playerRepository.findAll(); // обновление списка игроков
                playerService.checkLogin(PLAYER_LIST, login); // проверка на занятость логина
                player = playerFactory.makePlayer(login, password); // создание игрока
                player.setId(playerRepository.save(player)); // сохранение игрока в БД
                logging = loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_REGISTRATION); // логирование
                loggingRepository.save(logging); // сохранение лога успеха регистрации в БД
            } catch (InvalidCredentialException | SQLException | NullPointerException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.println("Успешная регистрация! \n");
            return;
        }
    }

    /**
     * Авторизация игрока в системе с сохранением в БД
     */
    private static void authorisation(BufferedReader bufferedReader) {
        String login;
        String password;

        while (true) {
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
            if (password.equals("Выход")) {
                return;
            }

            Connection connection = null;
            try {
                connection = DatabaseConnector.getConnection();
                playerRepository = new JdbcPlayer(connection); // соединение с БД для получения игроков
                loggingRepository = new JdbcLogging(connection); // соединение с БД для ведения аудита
                PLAYER_LIST = playerRepository.findAll(); // обновление списка игроков
                player = playerService.searchPlayer(PLAYER_LIST, login); // поиск игрока
                logging = playerService.authorisationPlayer(player, login, password); // авторизация
                loggingRepository.save(logging); // сохранение лога успеха авторизации в БД
            } catch (InvalidCredentialException e) {
                logging = loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_AUTH); // логирование
                loggingRepository.save(logging); // сохранение лога неудачной авторизации в БД
                System.out.println(e.getMessage());
                continue;
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return;
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            personalArea(bufferedReader); // инициализация входа в личный кабинет игрока
            return;
        }
    }

    /**
     * Личный кабинет игрока, заведение кошелька
     */
    private static void personalArea(BufferedReader bufferedReader) {
        System.out.printf("\n-------- Личный профиль %s -------- ", player.getLogin());
        System.out.printf("\nЗдравствуйте %s! \n", player.getLogin());

        while (true) {
            try (Connection connection = DatabaseConnector.getConnection()) {
                accountRepository = new JdbcAccount(connection); // соединение с БД для получения кошельков игроков
                List<Account> ACCOUNT_LIST = accountRepository.findAll(); // обновление списка кошельков
                account = accountService.searchAccountPlayer(ACCOUNT_LIST, player);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return;
            }
            if (account == null) { // Если у игрока нет кошелька
                account = createFirstAccount(bufferedReader); // вызов метода создания первого кошелька
                if (account == null) { // Повторная проверка
                    return;
                }
                continue;
            }
            playerManipulation(bufferedReader); // инициализация основного функционала игрока
            return;
        }
    }

    /**
     * Меню игрока для взаимодействия с кошельком (просмотр баланса, пополнение, списание, история операций),
     * также доступен просмотр всех операций игрока в системе(аудит игрока), для выхода предусмотрена деавторизация.
     */
    private static void playerManipulation(BufferedReader bufferedReader) {
        BigDecimal inputBigDecimal;
        String input;
        while (true) {
            System.out.println("\nВам доступны следующие функции: Просмотр баланса, Пополнение, Списание, Аудит игрока, История операций, Деавторизация");
            input = stringUserInput(bufferedReader);

            switch (input) {
                case "Просмотр баланса" -> {
                    System.out.println("-------- Ваш баланс -------- \n");
                    BigDecimal balance;
                    try (Connection connection = DatabaseConnector.getConnection()) {
                        accountRepository = new JdbcAccount(connection);
                        balance = accountRepository.findBalanceById(account.getId());
                    } catch (SQLException | RuntimeException e) {
                        System.err.println(e.getMessage());
                        continue;
                    }
                    accountService.printAccountBalance(balance);
                }
                case "Пополнение" -> {
                    String request = "credit";
                    System.out.println("-------- Пополнение -------- \n");
                    System.out.println("На сколько желаете пополнить счёт?");
                    inputBigDecimal = doubleUserInput(bufferedReader);

                    changeBalance(inputBigDecimal, request);
                }
                case "Списание" -> {
                    String request = "debit";
                    System.out.println("-------- Списание -------- \n");
                    System.out.println("Cколько желаете снять со счёта?");
                    inputBigDecimal = doubleUserInput(bufferedReader);

                    changeBalance(inputBigDecimal, request);
                }
                /*
                 * Аудит игрока, выводит в консоль время и тип всех операций игрока
                 */
                case "Аудит игрока" -> {
                    System.out.println("-------- Аудит игрока -------- \n");
                    System.out.println("Ваш список действий игрока:");
                    loggingService.printLogs(player); // вывод в консоль всех операций игрока
                }
                /*
                 * История денежных потоков игрока
                 */
                case "История операций" -> {
                    System.out.println("Ваш список транзакций кошелька:");
                    transactionService.printTransaction(account);
                }
                /*
                 * Деавторизация игрока
                 */
                case "Деавторизация" -> {
                    try (Connection connection = DatabaseConnector.getConnection()) {
                        loggingRepository = new JdbcLogging(connection);
                        logging = playerService.deauthorisationPlayer(player); // деавторизация и запись лога
                        loggingRepository.save(logging); // сохранение лога деавторизации
                        return;
                    } catch (SQLException e) {
                        System.out.println("Произошла ошибка соединения с БД");
                    }
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз. \n");
            }
        }
    }

    private static void changeBalance(BigDecimal inputBigDecimal, String request) {

        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
            loggingRepository = new JdbcLogging(connection);
            logging = accountService.performOperation(player, account, inputBigDecimal, request);
            loggingRepository.save(logging);
        } catch (RuntimeException | SQLException e) {
            Logging.TypeOperation typeOperation = request.equals("credit") ? Logging.TypeOperation.FAIL_CREDIT : Logging.TypeOperation.FAIL_DEBIT;
            logging = loggingFactory.makeLog(player, typeOperation);
            loggingRepository.save(logging);
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Предложение игроку создать свой первый кошелёк
     *
     * @return созданный аккаунт игрока
     */
    private static Account createFirstAccount(BufferedReader bufferedReader) {
        String input;

        while (true) {
            System.out.println("\n-------- Создание кошелька -------- \n");
            System.out.println("У вас нет активных счетов, создадим? Да/Нет");
            System.out.println("При выборе \"Нет\", будет произведена деавторизация.");
            input = stringUserInput(bufferedReader);


            switch (input) {
                case "Да" -> {
                    try (Connection connection = DatabaseConnector.getConnection()) {
                        accountRepository = new JdbcAccount(connection); // создание подключения к БД для получения кошельков игроков
                        loggingRepository = new JdbcLogging(connection); // создание подключения к БД для записи логов
                        account = accountFactory.makeAccount(player); // создание экземпляра кошелька игроку
                        account.setId(accountRepository.save(account)); // сохранение кошелька игрока в БД
                        logging = loggingFactory.makeLog(player, Logging.TypeOperation.SUCCES_CREATION_ACCOUNT); // логирование
                        loggingRepository.save(logging); // сохранение лога успешного создания кошелька игроку
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    System.out.printf("\nУспешно создан кошелёк! ID вашего кошелька № %d.", account.getId());
                    System.out.printf("\n-------- Личный профиль %s --------\n", player.getLogin());
                    return account;
                }
                case "Нет" -> {
                    try (Connection connection = DatabaseConnector.getConnection()) {
                        loggingRepository = new JdbcLogging(connection); // создание подключения к БД для записи логов
                        logging = loggingFactory.makeLog(player, Logging.TypeOperation.FAIL_CREATION_ACCOUNT); // логирование
                        loggingRepository.save(logging); // сохранение лога неудачного создания кошелька игроку
                        logging = playerService.deauthorisationPlayer(player); // деавторизация и запись лога
                        loggingRepository.save(logging); // сохранение лога деавторизации
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    return null;
                }
                default -> System.out.println("\nНеверный ввод. Пожалуйста, попробуйте ещё раз.");
            }
        }

    }

    /**
     * Перехватывание ошибки ввода-вывода с соответствующим сообщением
     *
     * @param e переменная исключения
     */
    private static void handleIOException(IOException e) {
        System.out.println("\nПроизошла ошибка ввода-вывода: " + e.getMessage());
    }
}
