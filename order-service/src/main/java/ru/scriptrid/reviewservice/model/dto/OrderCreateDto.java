package ru.scriptrid.reviewservice.model.dto;

public record OrderCreateDto(
        long productId,
        int quantity
) {
}
