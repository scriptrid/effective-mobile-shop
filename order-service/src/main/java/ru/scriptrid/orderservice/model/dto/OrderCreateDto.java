package ru.scriptrid.orderservice.model.dto;

public record OrderCreateDto(
        long id,
        int quantity
) {
}
