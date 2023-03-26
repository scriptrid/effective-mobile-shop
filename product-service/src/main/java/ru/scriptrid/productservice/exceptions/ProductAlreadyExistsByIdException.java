package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class ProductAlreadyExistsByIdException extends RuntimeException {

    private final String id;

    public ProductAlreadyExistsByIdException(String id) {
        this.id = id;
    }
}
