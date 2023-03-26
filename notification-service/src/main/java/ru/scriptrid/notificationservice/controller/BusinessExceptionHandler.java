package ru.scriptrid.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.scriptrid.notificationservice.exceptions.InvalidUserException;
import ru.scriptrid.notificationservice.exceptions.NotificationNotFoundException;

@ControllerAdvice
public class BusinessExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Void> onUserExists() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<Void> onInvalidUser() {
        return ResponseEntity.badRequest().build();
    }

}
