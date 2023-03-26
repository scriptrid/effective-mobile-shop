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

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<Void> onRequestNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<Void> onInsufficientQuantity() {
        return ResponseEntity.badRequest().build();
    }
}
