package ru.scriptrid.orderservice.exceptions;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FailedTransactionException extends RuntimeException {
    private final long sourceId;
    private final long destinationId;
    private final BigDecimal sourceDelta;
    private final BigDecimal destinationDelta;

    public FailedTransactionException(Throwable cause, long sourceId, long destinationId,
                                      BigDecimal sourceDelta, BigDecimal destinationDelta) {
        super(cause);
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.sourceDelta = sourceDelta;
        this.destinationDelta = destinationDelta;
    }
}
