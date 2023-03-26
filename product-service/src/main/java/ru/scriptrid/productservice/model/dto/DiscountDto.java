package ru.scriptrid.productservice.model.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

public record DiscountDto(
        long id,
        Set<Long> productIds,
        BigDecimal priceModifier,
        ZonedDateTime discountStart,
        ZonedDateTime discountEnd
) {
}
