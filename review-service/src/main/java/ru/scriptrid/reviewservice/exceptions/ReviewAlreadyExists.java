package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

@Getter
public class ReviewAlreadyExists extends RuntimeException {
    private final long userId;
    private final long productId;

    public ReviewAlreadyExists(long userId, long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
