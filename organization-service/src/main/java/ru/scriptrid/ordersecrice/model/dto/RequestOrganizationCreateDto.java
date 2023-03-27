package ru.scriptrid.ordersecrice.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record RequestOrganizationCreateDto(
        @NotBlank
        String name,

        @Nullable
        String logoUrl,

        @Nullable
        String description) {

}
