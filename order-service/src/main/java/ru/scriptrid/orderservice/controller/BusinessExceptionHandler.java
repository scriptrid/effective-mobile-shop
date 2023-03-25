package ru.scriptrid.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.orderservice.exceptions.OrderNotFoundException;
import ru.scriptrid.orderservice.exceptions.RefundTimeException;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FrozenOrganizationException.class)
    public ResponseEntity<Void> onFrozenOrganization() {
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(FrozenUserException.class)
    public ResponseEntity<Void> onFrozenUser() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> onNotFoundOrder() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(RefundTimeException.class)
    public ResponseEntity<Void> onRefundTimeGreaterThanOneDay() {
        return ResponseEntity.status(412).build();
    }

}
