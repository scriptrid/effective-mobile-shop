package ru.scriptrid.organizationservice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestOrganizationCreateDto(
        @NotBlank
        String name,
        String description) {

}
