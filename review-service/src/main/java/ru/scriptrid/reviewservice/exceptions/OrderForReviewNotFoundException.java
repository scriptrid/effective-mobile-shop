package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

@Getter
public class OrderForReviewNotFoundException extends RuntimeException {
    private final long orderId;

    public OrderForReviewNotFoundException(long orderId) {
        this.orderId = orderId;
    }
}
