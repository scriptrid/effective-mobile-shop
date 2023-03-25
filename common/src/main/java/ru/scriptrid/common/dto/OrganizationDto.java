package ru.scriptrid.common.dto;

public record OrganizationDto(
        long id,

        String name,

        boolean isFrozen,

        boolean isDeleted,

        long ownerId,

        String description) {
}
