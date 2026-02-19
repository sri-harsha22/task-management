package com.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating or updating a Task.
 *
 * This DTO is used for incoming API requests to create or update tasks.
 * It includes validation annotations to ensure data integrity at the API boundary.
 * Fields are nullable where appropriate to support partial updates (PATCH requests).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating or updating a task")
public class TaskRequestDTO {

    @NotBlank(message = "Task title is required and cannot be blank")
    @Size(min = 1, max = 100, message = "Task title must be between 1 and 100 characters")
    @Schema(description = "Title of the task", example = "Complete project documentation", minLength = 1, maxLength = 100)
    private String title;

    @Schema(description = "Detailed description of the task", example = "Create comprehensive documentation for the API endpoints")
    private String description;

    @Schema(description = "Whether the task is completed", example = "false")
    private Boolean isCompleted;

    @Schema(description = "Due date and time for the task", example = "2026-03-15T10:30:00")
    private LocalDateTime dueDate;

    @Schema(description = "Name or identifier of the person assigned to this task", example = "John Doe")
    private String assignedTo;

    @Schema(description = "Priority level of the task", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    private String priority;
}

