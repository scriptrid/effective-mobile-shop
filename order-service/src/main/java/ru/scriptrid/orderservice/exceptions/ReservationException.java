package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final long productId;

    private final int expectedQuantity;


    public ReservationException(Throwable cause, long productId, int expectedQuantity) {
        super(cause);
        this.productId = productId;
        this.expectedQuantity = expectedQuantity;
    }

    public ReservationException(long productId, int expectedQuantity) {
        this.productId = productId;
        this.expectedQuantity = expectedQuantity;
    }
}
