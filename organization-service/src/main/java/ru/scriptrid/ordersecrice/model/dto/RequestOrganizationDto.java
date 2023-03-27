package ru.scriptrid.ordersecrice.model.dto;

public record RequestOrganizationDto(
        long id,
        String name,
        String description,
        String logoUrl,
        long owner
) {
}
