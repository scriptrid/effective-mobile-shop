package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class ProductAlreadyExistsByNameException extends RuntimeException {
    private final String productName;

    public ProductAlreadyExistsByNameException(String productName) {

        this.productName = productName;
    }
}
