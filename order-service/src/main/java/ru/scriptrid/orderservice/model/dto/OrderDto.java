package ru.scriptrid.orderservice.model.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OrderDto(
        long id,
        ZonedDateTime timeOfOrder,
        long transactionId,
        long productId,
        long customerId,
        int quantityOfProduct,
        BigDecimal productPrice,
        BigDecimal totalAmount,
        boolean isReturned
) {
}
