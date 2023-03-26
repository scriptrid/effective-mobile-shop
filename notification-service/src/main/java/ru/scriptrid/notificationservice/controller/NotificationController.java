package ru.scriptrid.notificationservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.notificationservice.model.dto.NotificationCreateDto;
import ru.scriptrid.notificationservice.model.dto.NotificationDto;
import ru.scriptrid.notificationservice.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notification/")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public NotificationDto sendNotification(@RequestBody NotificationCreateDto dto) {
        return notificationService.sendNotification(dto);
    }

    @GetMapping("/{id}")
    public NotificationDto getNotification(@AuthenticationPrincipal JwtAuthenticationToken token, @PathVariable long id) {
        return notificationService.getNotification(token, id);
    }

    @GetMapping("/{userId}/all")
    public List<NotificationDto> getNotifications(@AuthenticationPrincipal JwtAuthenticationToken token, @PathVariable long userId) {
        return notificationService.getNotifications(token, userId);
    }
}
