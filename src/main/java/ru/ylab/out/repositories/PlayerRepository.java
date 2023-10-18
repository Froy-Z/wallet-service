package ru.ylab.out.repositories;

import ru.ylab.models.Player;

import java.util.List;

public interface PlayerRepository {
    void save(Player player);
    Player findById(Long playerId);
    List<Player> findAll();
    void delete(Long playerId);
}
