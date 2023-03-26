package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class StartedDiscountException extends RuntimeException {
    private final long id;

    public StartedDiscountException(long id) {
        this.id = id;
    }
}
