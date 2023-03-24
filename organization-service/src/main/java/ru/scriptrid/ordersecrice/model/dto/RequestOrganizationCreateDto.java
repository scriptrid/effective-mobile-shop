package ru.scriptrid.ordersecrice.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestOrganizationCreateDto(
        @NotBlank
        String name,
        String description) {

}
