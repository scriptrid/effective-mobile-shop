package ru.scriptrid.common.exception;


public class InvalidOwnerException extends RuntimeException {

    private final long entityId;
    private final long expectedOwner;
    private final long foundOwner;

    public InvalidOwnerException(long entityId, long expectedOwner, long foundOwner) {
        this.entityId = entityId;
        this.expectedOwner = expectedOwner;
        this.foundOwner = foundOwner;
    }

    public long getEntityId() {
        return entityId;
    }

    public long getExpectedOwner() {
        return expectedOwner;
    }

    public long getFoundOwner() {
        return foundOwner;
    }
}
