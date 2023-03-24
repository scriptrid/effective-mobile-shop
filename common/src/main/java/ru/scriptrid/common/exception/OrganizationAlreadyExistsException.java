package ru.scriptrid.common.exception;

public class OrganizationAlreadyExistsException extends RuntimeException {

    private final String organizationName;

    public OrganizationAlreadyExistsException(String organizationName) {
        this.organizationName = organizationName;
    }
}
