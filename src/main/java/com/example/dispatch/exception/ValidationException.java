package com.example.dispatch.exception;

/**
 * Thrown when request validation or business rules fail.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
