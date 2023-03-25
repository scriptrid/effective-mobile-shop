package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class ProductAlreadyExistsException extends RuntimeException {

    private final String id;

    public ProductAlreadyExistsException(String id) {
        this.id = id;
    }
}
