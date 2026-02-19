package com.taskmanagement.presentation.controller;

import com.taskmanagement.dto.TaskRequestDTO;
import com.taskmanagement.dto.TaskResponseDTO;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.TaskStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for Task API endpoints.
 *
 * Provides comprehensive endpoints for managing tasks including:
 * - CRUD operations
 * - Advanced filtering and searching
 * - Pagination and sorting
 * - Statistics and metrics
 *
 * All endpoints follow RESTful conventions and return appropriate HTTP status codes.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task Management API endpoints")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task.
     *
     * @param requestDTO the task data
     * @return the created task with HTTP 201 (Created)
     */
    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data provided")
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO requestDTO) {
        log.info("POST /tasks - Creating new task");
        TaskResponseDTO createdTask = taskService.createTask(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    /**
     * Retrieve a task by ID.
     *
     * @param id the task ID
     * @return the task with HTTP 200 (OK) or 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /tasks/{} - Retrieving task", id);
        TaskResponseDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Retrieve all tasks with pagination and sorting.
     *
     * @param pageable pagination parameters (page, size, sort)
     * @return a page of tasks with HTTP 200 (OK)
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks with support for pagination and sorting")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable) {
        log.info("GET /tasks - Retrieving all tasks");
        Page<TaskResponseDTO> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update a complete task.
     *
     * @param id the task ID
     * @param requestDTO the updated task data
     * @return the updated task with HTTP 200 (OK) or 404 (Not Found)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update entire task", description = "Updates all fields of a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid task data provided")
    })
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO requestDTO) {
        log.info("PUT /tasks/{} - Updating task", id);
        TaskResponseDTO updatedTask = taskService.updateTask(id, requestDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Partially update a task (only provided fields).
     *
     * @param id the task ID
     * @param requestDTO the partial task data
     * @return the updated task with HTTP 200 (OK) or 404 (Not Found)
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update task", description = "Updates only the provided fields of a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task partially updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponseDTO> partialUpdateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody TaskRequestDTO requestDTO) {
        log.info("PATCH /tasks/{} - Partially updating task", id);
        TaskResponseDTO updatedTask = taskService.partialUpdateTask(id, requestDTO);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task.
     *
     * @param id the task ID
     * @return HTTP 204 (No Content) or 404 (Not Found)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Permanently deletes a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tasks filtered by completion status.
     *
     * @param isCompleted the completion status filter
     * @param pageable pagination parameters
     * @return a page of filtered tasks with HTTP 200 (OK)
     */
    @GetMapping("/filter/completed")
    @Operation(summary = "Get tasks by completion status", description = "Retrieves tasks filtered by their completion status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByCompletionStatus(
            @Parameter(description = "Completion status filter", example = "false")
            @RequestParam Boolean isCompleted,
            Pageable pageable) {
        log.info("GET /tasks/filter/completed?isCompleted={} - Getting tasks by completion status", isCompleted);
        Page<TaskResponseDTO> tasks = taskService.getTasksByCompletionStatus(isCompleted, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks assigned to a specific person.
     *
     * @param assignedTo the assigned person name
     * @param pageable pagination parameters
     * @return a page of assigned tasks with HTTP 200 (OK)
     */
    @GetMapping("/filter/assigned-to")
    @Operation(summary = "Get tasks by assigned person", description = "Retrieves tasks assigned to a specific person")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved assigned tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByAssignedTo(
            @Parameter(description = "Name of the person assigned to", example = "John Doe")
            @RequestParam String assignedTo,
            Pageable pageable) {
        log.info("GET /tasks/filter/assigned-to?assignedTo={} - Getting tasks assigned to", assignedTo);
        Page<TaskResponseDTO> tasks = taskService.getTasksByAssignedTo(assignedTo, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks with due dates within a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination parameters
     * @return a page of tasks with HTTP 200 (OK)
     */
    @GetMapping("/filter/due-date-range")
    @Operation(summary = "Get tasks by due date range", description = "Retrieves tasks with due dates within the specified range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByDueDateRange(
            @Parameter(description = "Start date (ISO-8601)", example = "2026-02-20T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO-8601)", example = "2026-03-20T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        log.info("GET /tasks/filter/due-date-range - Getting tasks by due date range: {} to {}", startDate, endDate);
        Page<TaskResponseDTO> tasks = taskService.getTasksByDueDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get all overdue tasks.
     *
     * @param pageable pagination parameters
     * @return a page of overdue tasks with HTTP 200 (OK)
     */
    @GetMapping("/filter/overdue")
    @Operation(summary = "Get overdue tasks", description = "Retrieves all incomplete tasks with past due dates")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getOverdueTasks(Pageable pageable) {
        log.info("GET /tasks/filter/overdue - Getting overdue tasks");
        Page<TaskResponseDTO> tasks = taskService.getOverdueTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by priority level.
     *
     * @param priority the priority level
     * @param pageable pagination parameters
     * @return a page of tasks with HTTP 200 (OK)
     */
    @GetMapping("/filter/priority")
    @Operation(summary = "Get tasks by priority", description = "Retrieves tasks filtered by their priority level")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks"),
            @ApiResponse(responseCode = "400", description = "Invalid priority level")
    })
    public ResponseEntity<Page<TaskResponseDTO>> getTasksByPriority(
            @Parameter(description = "Priority level", example = "HIGH", schema = @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"}))
            @RequestParam String priority,
            Pageable pageable) {
        log.info("GET /tasks/filter/priority?priority={} - Getting tasks by priority", priority);
        Page<TaskResponseDTO> tasks = taskService.getTasksByPriority(priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks by title or description.
     *
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return a page of matching tasks with HTTP 200 (OK)
     */
    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Searches tasks by title or description containing the search term")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    public ResponseEntity<Page<TaskResponseDTO>> searchTasks(
            @Parameter(description = "Search term", example = "documentation")
            @RequestParam String searchTerm,
            Pageable pageable) {
        log.info("GET /tasks/search?searchTerm={} - Searching tasks", searchTerm);
        Page<TaskResponseDTO> tasks = taskService.searchTasks(searchTerm, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Mark a task as completed.
     *
     * @param id the task ID
     * @return the updated task with HTTP 200 (OK) or 404 (Not Found)
     */
    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark task as completed", description = "Marks a task as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as completed"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<TaskResponseDTO> markTaskAsCompleted(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("PUT /tasks/{}/complete - Marking task as completed", id);
        TaskResponseDTO completedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.ok(completedTask);
    }

    /**
     * Get task statistics.
     *
     * @return task statistics with HTTP 200 (OK)
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get task statistics", description = "Retrieves statistics about tasks including total, completed, and pending counts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics")
    public ResponseEntity<TaskStatistics> getTaskStatistics() {
        log.info("GET /tasks/statistics - Getting task statistics");
        TaskStatistics statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }
}

