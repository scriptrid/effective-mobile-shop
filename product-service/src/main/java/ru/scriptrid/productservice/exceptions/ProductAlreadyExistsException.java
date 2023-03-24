package ru.scriptrid.productservice.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {

    private final String id;

    public ProductAlreadyExistsException(String id) {
        this.id = id;
    }
}
