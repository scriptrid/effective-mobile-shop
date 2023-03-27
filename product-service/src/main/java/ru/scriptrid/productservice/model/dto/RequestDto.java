package ru.scriptrid.productservice.model.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record RequestDto(
        long id,
        String productName,
        String description,
        long organizationId,
        BigDecimal price,
        int quantityInStock,
        Set<String> tags,
        Map<String, String> specs
) {
}
