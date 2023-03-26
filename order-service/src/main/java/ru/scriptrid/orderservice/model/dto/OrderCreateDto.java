package ru.scriptrid.orderservice.model.dto;

public record OrderCreateDto(
        long productId,
        int quantity
) {
}
