package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

import java.time.Duration;

@Getter
public class RefundTimeException extends RuntimeException {
    private final Duration between;

    public RefundTimeException(Duration between) {
        this.between = between;
    }
}
