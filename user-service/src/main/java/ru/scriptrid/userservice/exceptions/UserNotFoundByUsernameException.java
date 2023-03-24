package ru.scriptrid.userservice.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundByUsernameException extends RuntimeException{
    private final String username;

    public UserNotFoundByUsernameException(String username) {
        this.username = username;
    }
}
