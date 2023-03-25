package ru.scriptrid.common.exception;

public class DeletedOrganizationException extends RuntimeException {
    private final long id;

    public DeletedOrganizationException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
