package ru.scriptrid.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.scriptrid.userservice.exceptions.*;


@ControllerAdvice
public class BusinessExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Void> onUserExists() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UserNotFoundByUsernameException.class)
    public ResponseEntity<Void> onUserNotFoundByName() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UserNotFoundByIdException.class)
    public ResponseEntity<Void> onUserNotFoundById() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Void> onTransactionNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Void> onInsufficientFunds() {
        return ResponseEntity.badRequest().build();
    }
}
