package ru.scriptrid.ordersecrice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.ordersecrice.exceptions.RequestOrganizationNotFoundException;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RequestOrganizationNotFoundException.class)
    public ResponseEntity<Void> onRequestOrganizationNotFound() {
        return ResponseEntity.notFound().build();
    }

}
