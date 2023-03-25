package ru.scriptrid.ordersecrice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationAlreadyExistsException;
import ru.scriptrid.common.exception.OrganizationNotFoundByIdException;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {
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

    @ExceptionHandler(InvalidOwnerException.class)
    public ResponseEntity<Void> onInvalidOwner() {
        return ResponseEntity.badRequest().build();
    }

}
