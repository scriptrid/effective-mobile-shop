package ru.scriptrid.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.orderservice.exceptions.*;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FrozenUserException.class)
    public ResponseEntity<Void> onFrozenUser() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(FailedTransactionException.class)
    public ResponseEntity<Void> onFailedTransaction() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(OrderAlreadyRefundedException.class)
    public ResponseEntity<Void> onAlreadyRefundedOrder() {
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

    @ExceptionHandler(ProductNotFoundForOrderException.class)
    public ResponseEntity<Void> onProductNotFoundForOrder() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UnableToGetProductException.class)
    public ResponseEntity<Void> onUnableToGetProduct() {
        return ResponseEntity.badRequest().build();
    }
}
