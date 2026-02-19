package com.taskmanagement.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for task statistics.
 *
 * This DTO encapsulates task-related statistics and metrics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task statistics and metrics")
public class TaskStatistics {

    @Schema(description = "Total number of tasks in the system", example = "100")
    private Long totalTasks;

    @Schema(description = "Number of completed tasks", example = "60")
    private Long completedTasks;

    @Schema(description = "Number of pending tasks", example = "40")
    private Long pendingTasks;

    @Schema(description = "Percentage of completed tasks", example = "60")
    private Long completionPercentage;
}

