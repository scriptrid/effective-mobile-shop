package ru.scriptrid.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.scriptrid.common.exception.*;

@ControllerAdvice
public class CommonBusinessExceptionHandler {
    @ExceptionHandler(OrganizationAlreadyExistsException.class)
    public ResponseEntity<Void> onOrganizationExists() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrganizationNotFoundByIdException.class)
    public ResponseEntity<Void> onOrganizationNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(FrozenOrganizationException.class)
    public ResponseEntity<Void> onFrozenOrganization() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DeletedOrganizationException.class)
    public ResponseEntity<Void> onDeletedOrganization() {
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(InvalidOwnerException.class)
    public ResponseEntity<Void> onInvalidOwner() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(FrozenUserException.class)
    public ResponseEntity<Void> onFrozenUser() {
        return ResponseEntity.badRequest().build();
    }
}
