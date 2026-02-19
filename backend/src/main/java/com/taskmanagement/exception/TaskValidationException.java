package com.taskmanagement.exception;

/**
 * Exception thrown when task validation fails.
 *
 * This exception is thrown when input validation constraints are violated.
 * It is typically caught and mapped to an HTTP 400 (Bad Request) response.
 */
public class TaskValidationException extends RuntimeException {

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

