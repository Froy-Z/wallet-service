package ru.ylab.interfaces;

import ru.ylab.exceptions.InvalidCredentialException;
import ru.ylab.models.Logging;
import ru.ylab.models.Player;

import java.util.List;
import java.util.Map;

public interface PlayerInterface {
    Logging authorisationPlayer(Map<String, String> credentials, List<Logging> loggingList, Player player, String login, String password) throws InvalidCredentialException;
    Logging deauthorisationPlayer(List<Logging> loggingList, Player player);
}
