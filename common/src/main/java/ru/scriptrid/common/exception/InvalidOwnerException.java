package ru.scriptrid.common.exception;


public class InvalidOwnerException extends RuntimeException {

    private final long organizationId;
    private final long expectedOwner;
    private final long foundOwner;

    public InvalidOwnerException(long organizationId, long expectedOwner, long foundOwner) {
        this.organizationId = organizationId;
        this.expectedOwner = expectedOwner;
        this.foundOwner = foundOwner;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public long getExpectedOwner() {
        return expectedOwner;
    }

    public long getFoundOwner() {
        return foundOwner;
    }
}
