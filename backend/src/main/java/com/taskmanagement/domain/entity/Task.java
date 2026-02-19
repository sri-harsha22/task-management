package com.taskmanagement.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Task entity representing a task in the system.
 *
 * This entity encapsulates all information about a task including:
 * - Core task properties (title, description, completion status, due date)
 * - Audit information (creation and modification timestamps, assignment tracking)
 * - Data persistence via JPA/Hibernate
 *
 * The entity uses Lombok annotations to minimize boilerplate code while maintaining
 * clean, readable code through auto-generated getters, setters, and constructors.
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    /**
     * Unique identifier for the task.
     * Auto-generated using a database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Title of the task.
     * Required field with a maximum length of 100 characters.
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Task title is required and cannot be blank")
    @Size(min = 1, max = 100, message = "Task title must be between 1 and 100 characters")
    private String title;

    /**
     * Detailed description of the task.
     * Optional field that can contain additional context about the task.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Indicates whether the task is completed.
     * Default value is false (task is not completed).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    /**
     * Due date and time for the task.
     * Optional field representing when the task should be completed.
     */
    @Column
    private LocalDateTime dueDate;

    /**
     * Timestamp when the task was created.
     * Automatically set to the current time when the task is first persisted.
     */
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when the task was last modified.
     * Automatically updated each time the task is modified.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime modifiedAt = LocalDateTime.now();

    /**
     * Name or identifier of the person/system to which this task is assigned.
     * Optional field for tracking task ownership.
     */
    @Column(length = 100)
    private String assignedTo;

    /**
     * Priority level of the task.
     * Uses an enumeration to ensure consistency.
     * Defaults to MEDIUM priority.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Update the modification timestamp to the current time.
     * Should be called whenever the task is modified.
     */
    public void updateModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }

    /**
     * Enumeration representing task priority levels.
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}

