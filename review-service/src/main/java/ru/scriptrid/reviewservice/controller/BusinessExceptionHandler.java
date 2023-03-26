package ru.scriptrid.reviewservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.scriptrid.common.exception.FrozenUserException;
import ru.scriptrid.reviewservice.exceptions.OrderForReviewNotFoundException;
import ru.scriptrid.reviewservice.exceptions.ReviewAlreadyExists;
import ru.scriptrid.reviewservice.exceptions.ReviewNotFoundByIdException;
import ru.scriptrid.reviewservice.exceptions.ReviewNotFoundByOrderIdException;


@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FrozenUserException.class)
    public ResponseEntity<Void> onFrozenUser() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ReviewAlreadyExists.class)
    public ResponseEntity<Void> onReviewAlreadyExists() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ReviewNotFoundByOrderIdException.class)
    public ResponseEntity<Void> onReviewNotFoundByOrderId() {
        return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(OrderForReviewNotFoundException.class)
    public ResponseEntity<Void> onOrderForReviewNotFound() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ReviewNotFoundByIdException.class)
    public ResponseEntity<Void> onReviewNotFoundById() {
        return ResponseEntity.notFound().build();
    }
}
