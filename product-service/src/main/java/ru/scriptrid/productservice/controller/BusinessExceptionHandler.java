package ru.scriptrid.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.DeletedOrganizationException;
import ru.scriptrid.productservice.exceptions.InsufficientQuantityException;
import ru.scriptrid.productservice.exceptions.ProductAlreadyExistsException;
import ru.scriptrid.productservice.exceptions.ProductNotFoundByIdException;

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

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<Void> onInsufficientQuantity() {
        return ResponseEntity.badRequest().build();
    }
    @ExceptionHandler(DeletedOrganizationException.class)
    public ResponseEntity<Void> onDeletedOrganization() {
        return ResponseEntity.notFound().build();
    }
}
