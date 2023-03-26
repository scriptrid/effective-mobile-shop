package ru.scriptrid.notificationservice.exceptions;

import lombok.Getter;

@Getter
public class NotificationNotFoundException extends RuntimeException {
    private final long id;

    public NotificationNotFoundException(long id) {
        this.id = id;
    }
}
