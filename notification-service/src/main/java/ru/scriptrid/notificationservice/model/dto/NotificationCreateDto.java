package ru.scriptrid.notificationservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record NotificationCreateDto(

        @NotNull
        long destinationId,

        @NotBlank
        String notificationHeader,

        @NotBlank
        String notificationText
) {
}
