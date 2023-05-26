package com.sharememories.sharememories.controller.advice;


import com.sharememories.sharememories.controller.api.*;
import com.sharememories.sharememories.exception.NotAuthenticatedException;
import com.sharememories.sharememories.util.ResponseEntityUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {CommentController.class, MessageController.class, PostController.class, ReactionController.class,
    UserController.class})
public class APIExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> messages = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(e -> e.getPropertyPath().toString(),
                        ConstraintViolation::getMessage));

        return ResponseEntityUtils.generateResponse(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return ResponseEntityUtils.generateResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        return ResponseEntityUtils.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<?> handleNotAuthenticatedException(NotAuthenticatedException ex) {
        return ResponseEntityUtils.generateResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
