package com.example.store;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        if ("id".equals(ex.getMessage())) {
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        } 
        return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
    }

}
