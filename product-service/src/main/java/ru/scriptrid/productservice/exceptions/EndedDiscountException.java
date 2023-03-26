package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class EndedDiscountException extends RuntimeException {
    private final long id;

    public EndedDiscountException(long id) {
        this.id = id;
    }
}
