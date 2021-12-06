package ru.otus.webserver.services;

public interface UserAuthService {
    boolean authenticate(String login, String password);
}
