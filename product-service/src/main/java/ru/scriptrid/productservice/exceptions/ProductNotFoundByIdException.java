package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundByIdException extends RuntimeException {
    private final long id;

    public ProductNotFoundByIdException(long id) {
        this.id = id;
    }
}
