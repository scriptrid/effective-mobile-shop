package ru.scriptrid.organizationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationAlreadyExistsException;
import ru.scriptrid.common.exception.OrganizationNotFoundException;
import ru.scriptrid.organizationservice.exceptions.NoSuchOrganizationsException;
import ru.scriptrid.organizationservice.exceptions.NoSuchRequestsException;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(OrganizationAlreadyExistsException.class)
    public ResponseEntity<Void> onOrganizationExists() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
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

    @ExceptionHandler(NoSuchRequestsException.class)
    public ResponseEntity<Void> onNoSuchRequests() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NoSuchOrganizationsException.class)
    public ResponseEntity<Void> onNoSuchOrganizations() {
        return ResponseEntity.notFound().build();
    }

}
