package ru.scriptrid.common.dto;

import java.math.BigDecimal;

public record TransactionCreateDto(
        long customerId,
        long sellerId,
        BigDecimal total,
        BigDecimal sellersIncome
) {
}
