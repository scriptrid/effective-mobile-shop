package ru.scriptrid.notificationservice.exceptions;

import lombok.Getter;

@Getter
public class InvalidUserException extends RuntimeException {
    private final long expectedId;
    private final long foundId;

    public InvalidUserException(long expectedId, long foundId) {
        this.expectedId = expectedId;
        this.foundId = foundId;
    }
}
