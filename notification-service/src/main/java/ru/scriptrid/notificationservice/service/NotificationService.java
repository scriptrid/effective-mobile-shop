package ru.scriptrid.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.exception.DeletedUserException;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.notificationservice.exceptions.InvalidUserException;
import ru.scriptrid.notificationservice.exceptions.NotificationNotFoundException;
import ru.scriptrid.notificationservice.model.dto.NotificationCreateDto;
import ru.scriptrid.notificationservice.model.dto.NotificationDto;
import ru.scriptrid.notificationservice.model.entity.NotificationEntity;
import ru.scriptrid.notificationservice.repository.NotificationRepository;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    private final WebUserService webUserService;

    public NotificationService(WebUserService webUserService,
                               NotificationRepository notificationRepository) {
        this.webUserService = webUserService;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public NotificationDto sendNotification(NotificationCreateDto dto) {
        UserDto destinationUser = webUserService.getDto(dto.destinationId());
        if (destinationUser.isDeleted()) {
            throw new DeletedUserException(destinationUser.id());
        }
        if (destinationUser.isFrozen()) {
            throw new FrozenUserException(destinationUser.id());
        }
        NotificationEntity notificationEntity = notificationRepository.save(toEntity(dto));
        return toDto(notificationEntity);
    }

    @Transactional
    public NotificationDto getNotification(JwtAuthenticationToken token, long id) {
        NotificationEntity notification = getNotificationById(id);
        if (token.getId() != notification.getDestinationId()) {
            if (token.isAdmin()) {
                return toDto(notification);
            }
            log.warn("User with id \"{}\" is not an owner of notification with id \"{}\"", token.getId(), id);
            throw new InvalidOwnerException(id, notification.getDestinationId(), token.getId());
        }
        return toDto(notification);
    }

    private NotificationEntity getNotificationById(long id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Notification with id \"{}\" not found", id);
                    return new NotificationNotFoundException(id);
                }
        );
    }

    private NotificationDto toDto(NotificationEntity notificationEntity) {
        return new NotificationDto(
                notificationEntity.getId(),
                notificationEntity.getDestinationId(),
                notificationEntity.getNotificationHeader(),
                notificationEntity.getNotificationText()
        );
    }

    private NotificationEntity toEntity(NotificationCreateDto dto) {
        NotificationEntity entity = new NotificationEntity();
        entity.setDestinationId(dto.destinationId());
        entity.setNotificationHeader(dto.notificationHeader());
        entity.setNotificationText(dto.notificationText());

        return entity;
    }

    @Transactional
    public List<NotificationDto> getNotifications(JwtAuthenticationToken token, long userId) {
        UserDto dto = webUserService.getDto(userId);
        if (dto.isDeleted()) {
            log.warn("User with id \"{}\" is deleted", userId);
            throw new DeletedUserException(userId);
        }
        if (token.getId() != userId) {
            if (token.isAdmin()) {
                return getNotificationsByUserId(userId);
            }
            log.warn("User with id \"{}\" tried to get other user`s with id \"{}\" notifications ", token.getId(), userId);
            throw new InvalidUserException(userId, token.getId());
        }
        if (dto.isFrozen()) {
            log.warn("User with id \"{}\" is frozen", userId);
            throw new FrozenUserException(userId);
        }
        return getNotificationsByUserId(userId);
    }

    private List<NotificationDto> getNotificationsByUserId(long userId) {
        return notificationRepository.findByDestinationId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
