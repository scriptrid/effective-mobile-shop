package ru.scriptrid.organizationservice.model.dto;

public record RequestOrganizationDto(
        long requestId,
        String organizationName,
        String organizationDescription,
        String organizationOwner
) {
}
