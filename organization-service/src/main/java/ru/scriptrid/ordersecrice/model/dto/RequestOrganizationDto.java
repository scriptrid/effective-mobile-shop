package ru.scriptrid.ordersecrice.model.dto;

public record RequestOrganizationDto(
        long requestId,
        String organizationName,
        String organizationDescription,
        long organizationOwner
) {
}
