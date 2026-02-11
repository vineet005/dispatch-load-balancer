package com.example.dispatch.exception;

import com.example.dispatch.dto.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Dispatch application.
 * <p>
 * This class handles various types of exceptions thrown across controllers and services
 * and provides consistent, structured API responses.
 * It uses {@code @RestControllerAdvice} to intercept exceptions globally.
 * </p>
 *
 * <p>Response format:</p>
 * <pre>
 * {
 *   "status": "error",
 *   "message": "Error message",
 *   "errors": { "field": "validation message" } // optional
 * }
 * </pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom validation exceptions.
     *
     * @param ex the {@link ValidationException} thrown
     * @return {@link ResponseEntity} with status 400 and an {@link ApiResponse} body
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(ex.getMessage(), "error"));
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param ex the {@link ResourceNotFoundException} thrown
     * @return {@link ResponseEntity} with status 404 and an {@link ApiResponse} body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(ex.getMessage(), "error"));
    }

    /**
     * Handles {@link MethodArgumentNotValidException}, which occurs when
     * @Valid annotated request body fails validation.
     *
     * @param ex the exception containing binding errors
     * @return {@link ResponseEntity} with status 400 and a detailed error map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles {@link ConstraintViolationException}, usually thrown when
     * method parameters violate constraints.
     *
     * @param ex the exception containing constraint violations
     * @return {@link ResponseEntity} with status 400 and {@link ApiResponse} body
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("Validation failed: " + ex.getMessage(), "error"));
    }

    /**
     * Handles {@link DataIntegrityViolationException}, typically when
     * database constraints (like unique keys) are violated.
     *
     * @param ex the exception thrown during DB operations
     * @return {@link ResponseEntity} with status 409 and {@link ApiResponse} body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDuplicate(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse("Duplicate resource detected", "error"));
    }

    /**
     * Handles generic exceptions that are not explicitly handled by other methods.
     *
     * @param ex the exception thrown
     * @return {@link ResponseEntity} with status 500 and {@link ApiResponse} body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
        ex.printStackTrace(); // Important for debugging
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Internal server error", "error"));
    }

    /**
     * Handles {@link HandlerMethodValidationException}, which occurs when
     * method parameters fail validation.
     *
     * @param ex the exception containing parameter validation results
     * @return {@link ResponseEntity} with status 400 and a detailed error map
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodValidation(
            HandlerMethodValidationException ex) {

        Map<String, String> errors = new HashMap<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            result.getResolvableErrors().forEach(error -> {
                String fieldName = result.getMethodParameter().getParameterName();
                errors.put(fieldName, error.getDefaultMessage());
            });
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles {@link HttpMessageNotReadableException}, typically thrown
     * when the request JSON cannot be parsed.
     *
     * @param ex the exception containing the parse error
     * @return {@link ResponseEntity} with status 400 and a detailed message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(
            HttpMessageNotReadableException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalidFormat) {
            String fieldName = invalidFormat.getPath()
                    .stream()
                    .map(ref -> ref.getFieldName())
                    .reduce((first, second) -> second)
                    .orElse("unknown");

            if (invalidFormat.getTargetType().isEnum()) {
                Object[] allowedValues = invalidFormat.getTargetType().getEnumConstants();
                response.put("message",
                        "Invalid value for field '" + fieldName +
                                "'. Allowed values are: " +
                                Arrays.toString(allowedValues));
                return ResponseEntity.badRequest().body(response);
            }
        }

        response.put("message", "Malformed JSON request");
        return ResponseEntity.badRequest().body(response);
    }
}
