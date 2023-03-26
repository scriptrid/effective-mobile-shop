package ru.scriptrid.common.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record TransactionDto(
        long id,
        long sourceId,
        long destinationId,
        BigDecimal sourceDelta,
        BigDecimal destinationDelta,
        boolean isReturn,
        ZonedDateTime timeOfTransaction
) {
}
