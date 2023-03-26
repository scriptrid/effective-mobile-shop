package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class InvalidTimeException extends RuntimeException {
    private final ZonedDateTime dateTime;

    public InvalidTimeException(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
