package ru.scriptrid.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.productservice.exceptions.*;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ProductAlreadyExistsByIdException.class)
    public ResponseEntity<Void> onProductExistsById() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ProductAlreadyExistsByNameException.class)
    public ResponseEntity<Void> onProductExistsByName() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ProductNotFoundByIdException.class)
    public ResponseEntity<Void> onProductNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidProductsException.class)
    public ResponseEntity<Void> onInvalidProducts() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<Void> onInvalidTime() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<Void> onRequestNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<Void> onInsufficientQuantity() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(DiscountNotFoundException.class)
    public ResponseEntity<Void> onDiscountNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EndedDiscountException.class)
    public ResponseEntity<Void> onEndedDiscount() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StartedDiscountException.class)
    public ResponseEntity<Void> onStartedDiscount() {
        return ResponseEntity.notFound().build();
    }
}
