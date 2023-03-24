package ru.scriptrid.ordersecrice.model.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record EditOrganizationDto(
        @NotBlank
        String name,

        String description
) {
}

