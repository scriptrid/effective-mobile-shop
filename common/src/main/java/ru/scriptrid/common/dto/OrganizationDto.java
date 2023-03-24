package ru.scriptrid.common.dto;

public record OrganizationDto(
        long id,

        String name,

        boolean isFrozen,

        String owner,

        String description) {
}
