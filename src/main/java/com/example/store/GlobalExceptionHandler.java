package com.example.store;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized error handler, leveraging the web-friendly implementation from
 * ResponseEntityExceptionHandler.
 * Remaining in the same line, the two custom handlers are responding with
 * ProblemDetail objects.
 * 
 * The catch-all handleException does not send error message details, to protect
 * possibly sensitive
 * information.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Needed for some of the params validation - e.g. UUID
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.warn(ex.getMessage());
        return new ResponseEntity<>(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
    }

    // Catch-all exception handler for anything not explicitly handled by either
    // ResponseEntityExceptionHandler or handleConstraintViolationException.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Override handleExceptionInternal to include logging of errors. WARN is used
    // as most are under clients' control and not actionable.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex,
            @Nullable Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {
        logger.warn(ex.getMessage());
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
