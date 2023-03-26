package ru.scriptrid.notificationservice.model.dto;

public record NotificationCreateDto(
        long destinationId,
        String notificationHeader,
        String notificationText
) {
}
