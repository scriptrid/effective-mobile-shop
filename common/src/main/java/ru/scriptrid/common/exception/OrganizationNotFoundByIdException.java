package ru.scriptrid.common.exception;
public class OrganizationNotFoundByIdException extends RuntimeException {

    private final long id;

    public OrganizationNotFoundByIdException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
