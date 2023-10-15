package ru.ylab.models;


public class Player {
    private final Long id;
    private final String login;
    private final String password;


    public Player(Long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

}
