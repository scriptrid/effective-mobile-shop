package ru.scriptrid.common.dto;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record ProductDto(
        long id,

        String productName,

        String description,

        long organizationId,

        BigDecimal price,

        int quantityInStock,

        Set<String> tags,

        Map<String,String> specs,

        @Nullable
        BigDecimal priceModifier
) {
}
