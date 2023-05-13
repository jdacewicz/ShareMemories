package com.sharememories.sharememories.controller.advice;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("status", HttpStatus.BAD_REQUEST.value());
        output.put("messages", ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(e -> e.getPropertyPath().toString(),
                        ConstraintViolation::getMessage)));

        return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .headers(new HttpHeaders())
               .body(output);
    }
}
