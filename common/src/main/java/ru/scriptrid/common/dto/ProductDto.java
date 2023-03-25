package ru.scriptrid.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDto(
        long id,

        String productName,

        String description,

        long organizationId,

        BigDecimal price,

        int quantityInStock,

        List<String> tags,

        String specs
) {
}