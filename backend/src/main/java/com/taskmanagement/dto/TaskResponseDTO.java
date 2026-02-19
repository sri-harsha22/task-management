package com.taskmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Task API responses.
 *
 * This DTO is used for outgoing API responses when returning task information.
 * It includes all task properties and is suitable for serialization to JSON.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO containing complete task information")
public class TaskResponseDTO {

    @Schema(description = "Unique identifier of the task", example = "1")
    private Long id;

    @Schema(description = "Title of the task", example = "Complete project documentation")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Create comprehensive documentation for the API endpoints")
    private String description;

    @Schema(description = "Whether the task is completed", example = "false")
    private Boolean isCompleted;

    @Schema(description = "Due date and time for the task", example = "2026-03-15T10:30:00")
    private LocalDateTime dueDate;

    @Schema(description = "Timestamp when the task was created", example = "2026-02-20T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the task was last modified", example = "2026-02-20T10:30:00")
    private LocalDateTime modifiedAt;

    @Schema(description = "Name or identifier of the person assigned to this task", example = "John Doe")
    private String assignedTo;

    @Schema(description = "Priority level of the task", example = "HIGH")
    private String priority;
}

