package ru.scriptrid.common.exception;

public class InvalidOwnerException extends RuntimeException {

    private final long organizationId;
    private final String expectedOwner;
    private final String foundOwner;

    public InvalidOwnerException(long organizationId, String expectedOwner, String foundOwner) {
        this.organizationId = organizationId;
        this.expectedOwner = expectedOwner;
        this.foundOwner = foundOwner;
    }
}
