package ru.scriptrid.notificationservice.model.dto;

public record NotificationDto(
        long notificationId,

        long destinationId,

        String notificationHeader,

        String notificationText

) {
}
