package ru.scriptrid.productservice.model.dto;

import java.util.List;

public record ProductDto(
        long id,

        String productName,

        String description,

        long organizationId,

        double price,

        int quantityInStock,

        List<String> tags,

        String specs
) {
}
