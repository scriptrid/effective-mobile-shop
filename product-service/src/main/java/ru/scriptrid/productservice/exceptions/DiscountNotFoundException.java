package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class DiscountNotFoundException extends RuntimeException {
    private final long id;

    public DiscountNotFoundException(long id) {
        this.id = id;
    }
}
