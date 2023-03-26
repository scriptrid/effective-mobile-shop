package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

@Getter
public class InvalidCustomerException extends RuntimeException {
    private final long orderId;
    private final long customerId;

    public InvalidCustomerException(long orderId, long customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
}
