package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundForOrderException extends RuntimeException {
    private final long id;

    public ProductNotFoundForOrderException(long id) {
        this.id = id;
    }
}
