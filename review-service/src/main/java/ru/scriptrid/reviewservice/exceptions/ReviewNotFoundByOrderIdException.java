package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

@Getter
public class ReviewNotFoundByOrderIdException extends RuntimeException {
    private final long orderId;

    public ReviewNotFoundByOrderIdException(long orderId) {
        this.orderId = orderId;
    }
}
