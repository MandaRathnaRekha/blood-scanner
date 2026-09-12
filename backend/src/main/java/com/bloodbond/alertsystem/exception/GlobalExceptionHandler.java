package com.bloodbond.alertsystem.exception;

import com.bloodbond.alertsystem.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * LifePulse - Emergency Blood Alert System
 * GlobalExceptionHandler: Centralized Error Interceptor
 * 
 * Intercepts uncaught exceptions and validation failures across all REST controllers,
 * transforming Java stack traces into clean, standardized JSON ApiResponse envelopes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepts DTO validation errors triggered by @Valid annotations.
     * Extracts field-level error messages and returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        StringBuilder combinedMessage = new StringBuilder("Validation failed: ");

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            combinedMessage.append(error.getField())
                           .append(" (")
                           .append(error.getDefaultMessage())
                           .append("); ");
        }

        logger.warn("Validation error on request: {}", combinedMessage);

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                combinedMessage.toString(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Catches invalid client input or query arguments.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument encountered: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Fallback handler for any unexpected system or database errors.
     * Prevents raw stack traces from leaking to public clients.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        logger.error("Unhandled system exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal emergency server error. Please retry or dial helpline 108."));
    }
}
