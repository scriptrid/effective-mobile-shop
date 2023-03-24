package ru.scriptrid.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.productservice.exceptions.*;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Void> onProductExists() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ProductNotFoundByIdException.class)
    public ResponseEntity<Void> onProductNotFound() {
        return ResponseEntity.notFound().build();
    }
}
