package com.taskmanagement.exception;

/**
 * Centralized error messages for task validation and operations
 *
 * This class contains all error message constants used throughout
 * the application to ensure consistency and facilitate localization.
 */
public final class ErrorMessages {

    // Task validation messages
    public static final String TASK_TITLE_REQUIRED = "Task title is required";
    public static final String TASK_TITLE_EXCEEDS_MAX_LENGTH = "Task title cannot exceed 100 characters";
    public static final String TASK_TITLE_MIN_LENGTH = "Task title must be at least 1 character";
    public static final String TASK_DESCRIPTION_EXCEEDS_MAX_LENGTH = "Task description cannot exceed 1000 characters";
    public static final String TASK_ASSIGNEE_EXCEEDS_MAX_LENGTH = "Assigned to name cannot exceed 100 characters";

    // Date validation messages
    public static final String INVALID_DATE_RANGE = "Start date must be before end date";
    public static final String DUE_DATE_IN_PAST = "Task due date is in the past";
    public static final String INVALID_DUE_DATE = "Due date cannot be null";

    // Priority validation messages
    public static final String INVALID_PRIORITY = "Invalid priority level. Allowed values: LOW, MEDIUM, HIGH, URGENT";

    // Task not found messages
    public static final String TASK_NOT_FOUND = "Task not found with id: ";
    public static final String TASK_NOT_FOUND_FOR_UPDATE = "Cannot update task. Task not found with id: ";
    public static final String TASK_NOT_FOUND_FOR_DELETE = "Cannot delete task. Task not found with id: ";
    public static final String TASK_NOT_FOUND_FOR_COMPLETION = "Cannot mark task complete. Task not found with id: ";

    // General error messages
    public static final String TASK_OPERATION_FAILED = "Task operation failed";
    public static final String INVALID_TASK_DATA = "Invalid task data provided";

    // Success messages (for logging)
    public static final String TASK_CREATED_SUCCESS = "Task created successfully";
    public static final String TASK_UPDATED_SUCCESS = "Task updated successfully";
    public static final String TASK_DELETED_SUCCESS = "Task deleted successfully";
    public static final String TASK_MARKED_COMPLETE_SUCCESS = "Task marked as completed successfully";

    // Log messages
    public static final String CREATING_TASK_WITH_TITLE = "Creating new task with title: {}";
    public static final String TASK_CREATED_WITH_ID = "Task created successfully with id: {}";
    public static final String UPDATING_TASK_WITH_ID = "Updating task with id: {}";
    public static final String TASK_UPDATED_WITH_ID = "Task with id: {} updated successfully";
    public static final String PARTIALLY_UPDATING_TASK = "Partially updating task with id: {}";
    public static final String DELETING_TASK_WITH_ID = "Deleting task with id: {}";
    public static final String TASK_DELETED_WITH_ID = "Task with id: {} deleted successfully";
    public static final String MARKING_TASK_COMPLETE = "Marking task with id: {} as completed";
    public static final String TASK_MARKED_COMPLETE = "Task with id: {} marked as completed";

    // Query log messages
    public static final String FETCHING_TASK_BY_ID = "Fetching task with id: {}";
    public static final String FETCHING_ALL_TASKS = "Fetching all tasks with pageable: {}";
    public static final String FETCHING_TASKS_BY_STATUS = "Fetching tasks with completion status: {} and pageable: {}";
    public static final String FETCHING_TASKS_BY_ASSIGNEE = "Fetching tasks assigned to: {} with pageable: {}";
    public static final String FETCHING_TASKS_BY_DATE_RANGE = "Fetching tasks with due date range from {} to {} with pageable: {}";
    public static final String FETCHING_OVERDUE_TASKS = "Fetching overdue tasks with pageable: {}";
    public static final String FETCHING_TASKS_BY_PRIORITY = "Fetching tasks with priority: {} and pageable: {}";
    public static final String SEARCHING_TASKS = "Searching tasks with term: {} and pageable: {}";
    public static final String FETCHING_STATISTICS = "Fetching task statistics";

    // Warning messages
    public static final String WARNING_DUE_DATE_IN_PAST = "Task due date is in the past: {}";

    // Private constructor to prevent instantiation
    private ErrorMessages() {
        throw new AssertionError("Cannot instantiate ErrorMessages utility class");
    }
}

