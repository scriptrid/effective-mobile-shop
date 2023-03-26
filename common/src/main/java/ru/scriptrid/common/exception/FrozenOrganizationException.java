package ru.scriptrid.common.exception;

public class FrozenOrganizationException extends RuntimeException {
    private final long id;

    public FrozenOrganizationException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
