package ru.scriptrid.common.exception;

public class DeletedUserException extends RuntimeException {
    private final long id;

    public DeletedUserException(long id) {

        this.id = id;
    }

    public long getId() {
        return id;
    }
}
