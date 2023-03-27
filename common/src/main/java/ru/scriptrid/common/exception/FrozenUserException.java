package ru.scriptrid.common.exception;

public class FrozenUserException extends RuntimeException {
    private final long id;

    public FrozenUserException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
