package ru.ylab.in.factories;

import ru.ylab.models.Account;
import ru.ylab.models.Player;

import java.util.List;

public class AccountFactory {

    /**
     * Создание кошелька игроку
     * @param accountList коллекция кошельков всех игроков
     * @param player игрок для создания кошелька
     * @return новый объект Account
     */
    public Account makeAccount(List<Account> accountList, Player player) {
        Account account;
        if (!accountList.isEmpty()) {
            int sizeList = accountList.size();
            Long lastPlayerId = accountList.get(sizeList - 1).getPlayerId() + 1L;
            account = new Account(sizeList + 1L, lastPlayerId);
        } else {
            account = new Account(1L, player.getId());
        }
        return account;
    }
}
