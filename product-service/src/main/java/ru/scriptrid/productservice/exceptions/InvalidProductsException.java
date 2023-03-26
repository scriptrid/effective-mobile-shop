package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

import java.util.Set;

@Getter
public class InvalidProductsException extends RuntimeException {
    private final Set<Long> productIds;

    public InvalidProductsException(Set<Long> productIds) {
        this.productIds = productIds;
    }
}
