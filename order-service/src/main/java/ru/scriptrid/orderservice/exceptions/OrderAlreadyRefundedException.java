package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

@Getter
public class OrderAlreadyRefundedException extends RuntimeException {
    private final long orderId;

    public OrderAlreadyRefundedException(long orderId) {
        this.orderId = orderId;
    }
}
