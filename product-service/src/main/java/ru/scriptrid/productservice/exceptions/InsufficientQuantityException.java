package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class InsufficientQuantityException extends RuntimeException {
    private final int quantityInStock;
    private final int requestedQuantity;

    public InsufficientQuantityException(int quantityInStock, int requestedQuantity) {
        this.quantityInStock = quantityInStock;
        this.requestedQuantity = requestedQuantity;
    }
}
