package com.taskmanagement.exception;

/**
 * Exception thrown when a requested task resource is not found.
 *
 * This exception is typically caught and mapped to an HTTP 404 (Not Found) response.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static TaskNotFoundException withTaskId(Long taskId) {
        return new TaskNotFoundException("Task not found with id: " + taskId);
    }
}

