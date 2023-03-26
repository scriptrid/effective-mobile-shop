package ru.scriptrid.reviewservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.reviewservice.exceptions.OrderNotFoundException;
import ru.scriptrid.reviewservice.exceptions.RefundTimeException;
import ru.scriptrid.reviewservice.exceptions.ReservationException;


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
    public ResponseEntity<Void> onRefundTimeRanOut() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<Void> onFailedReservation() {
        return ResponseEntity.badRequest().build();
    }

}
