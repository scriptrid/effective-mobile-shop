package ru.scriptrid.userservice.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    private final String username;

    public UsernameAlreadyExistsException(String username) {
        this.username = username;
    }
}
