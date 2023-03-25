package ru.scriptrid.ordersecrice.exceptions;

import lombok.Getter;

@Getter
public class RequestOrganizationNotFoundException extends RuntimeException {

    private final long id;

    public RequestOrganizationNotFoundException(long id) {
        this.id = id;
    }
}
