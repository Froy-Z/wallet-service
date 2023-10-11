package ru.ylab.in.factories;

import ru.ylab.models.Account;
import ru.ylab.models.Player;

import java.util.List;

public class AccountFactory {

    /**
     * Создание кошелька игроку
     * @param accountList коллекция кошельков всех игроков
     * @param player игрок для создания кошелька
     */
    public void makeAccount(List<Account> accountList, Player player) {

        if (!accountList.isEmpty()) {

            int sizeList = accountList.size();
            Long lastPlayerId = accountList.get(sizeList - 1).getPlayerId() + 1L;
            accountList.add(new Account(sizeList + 1L, lastPlayerId));

            System.out.println("Успешно создан кошелёк!");
        }

        accountList.add(new Account(1L, player.getId()));
    }
}
