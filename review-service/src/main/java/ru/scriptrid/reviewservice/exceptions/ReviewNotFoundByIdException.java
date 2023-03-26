package ru.scriptrid.reviewservice.exceptions;

import lombok.Getter;

@Getter
public class ReviewNotFoundByIdException extends RuntimeException {
    private final long id;

    public ReviewNotFoundByIdException(long id) {
        this.id = id;
    }
}
