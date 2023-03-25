package ru.scriptrid.productservice.exceptions;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends RuntimeException {
    private final long id;

    public RequestNotFoundException(long id) {
        this.id = id;
    }
}
