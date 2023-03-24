package ru.scriptrid.userservice.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundByIdException extends RuntimeException{
    private final long id;

    public UserNotFoundByIdException(long id) {
        this.id = id;
    }
}
