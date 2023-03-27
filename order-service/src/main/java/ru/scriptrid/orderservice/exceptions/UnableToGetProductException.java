package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

@Getter
public class UnableToGetProductException extends RuntimeException {
    private final long productId;

    public UnableToGetProductException(Throwable e, long productId) {
        super(e);
        this.productId = productId;
    }
}
