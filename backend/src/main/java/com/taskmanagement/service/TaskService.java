package com.taskmanagement.service;

import com.taskmanagement.domain.entity.Task;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.dto.TaskRequestDTO;
import com.taskmanagement.dto.TaskResponseDTO;
import com.taskmanagement.exception.ErrorMessages;
import com.taskmanagement.exception.TaskNotFoundException;
import com.taskmanagement.exception.TaskValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service layer for Task business logic.
 *
 * This service encapsulates all business operations for tasks including:
 * - CRUD operations with validation
 * - Complex queries with filtering, sorting, and pagination
 * - Data transformation between DTOs and entities
 * - Transaction management and error handling
 *
 * Following the Service pattern to separate business logic from presentation and persistence layers,
 * ensuring loose coupling and high cohesion.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Create a new task.
     *
     * @param requestDTO the task data from the API request
     * @return the created task as a response DTO
     * @throws TaskValidationException if task data is invalid
     */
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        log.info(ErrorMessages.CREATING_TASK_WITH_TITLE, requestDTO.getTitle());

        validateTaskRequestAndThrowIfInvalid(requestDTO);

        Task task = buildTaskFromRequest(requestDTO);
        Task savedTask = taskRepository.save(task);

        log.info(ErrorMessages.TASK_CREATED_WITH_ID, savedTask.getId());
        return convertToResponseDTO(savedTask);
    }

    /**
     * Retrieve a task by its ID.
     *
     * @param id the task ID
     * @return the task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        log.debug(ErrorMessages.FETCHING_TASK_BY_ID, id);

        Task task = findTaskByIdOrThrowNotFoundException(id);
        return convertToResponseDTO(task);
    }

    /**
     * Retrieve all tasks with pagination and sorting support.
     *
     * @param pageable pagination and sorting information
     * @return a page of tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_ALL_TASKS, pageable);
        return taskRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    /**
     * Update an existing task.
     *
     * @param id the task ID
     * @param requestDTO the updated task data
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     * @throws TaskValidationException if updated data is invalid
     */
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        log.info(ErrorMessages.UPDATING_TASK_WITH_ID, id);

        Task task = findTaskByIdOrThrowNotFoundException(id);
        validateTaskRequestAndThrowIfInvalid(requestDTO);

        updateTaskFromRequest(task, requestDTO);
        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info(ErrorMessages.TASK_UPDATED_WITH_ID, id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Partially update a task (PATCH operation).
     *
     * @param id the task ID
     * @param requestDTO the partial task data
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskResponseDTO partialUpdateTask(Long id, TaskRequestDTO requestDTO) {
        log.info(ErrorMessages.PARTIALLY_UPDATING_TASK, id);

        Task task = findTaskByIdOrThrowNotFoundException(id);
        updateTaskFieldsIfPresent(task, requestDTO);

        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info(ErrorMessages.TASK_UPDATED_WITH_ID, id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Delete a task by its ID.
     *
     * @param id the task ID
     * @throws TaskNotFoundException if the task does not exist
     */
    public void deleteTask(Long id) {
        log.info(ErrorMessages.DELETING_TASK_WITH_ID, id);

        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException.withTaskId(id);
        }

        taskRepository.deleteById(id);
        log.info(ErrorMessages.TASK_DELETED_WITH_ID, id);
    }

    /**
     * Get all tasks filtered by completion status.
     *
     * @param isCompleted the completion status filter
     * @param pageable pagination and sorting information
     * @return a page of filtered tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByCompletionStatus(Boolean isCompleted, Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_TASKS_BY_STATUS, isCompleted, pageable);
        return taskRepository.findByIsCompleted(isCompleted, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks assigned to a specific person.
     *
     * @param assignedTo the person name
     * @param pageable pagination and sorting information
     * @return a page of tasks assigned to the specified person
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByAssignedTo(String assignedTo, Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_TASKS_BY_ASSIGNEE, assignedTo, pageable);
        return taskRepository.findByAssignedTo(assignedTo, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks with due dates in a specified range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @param pageable pagination and sorting information
     * @return a page of tasks with due dates in the specified range
     * @throws TaskValidationException if date range is invalid
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByDueDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_TASKS_BY_DATE_RANGE, startDate, endDate, pageable);

        validateDateRange(startDate, endDate);
        return taskRepository.findByDueDateRange(startDate, endDate, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all overdue tasks (incomplete tasks with past due dates).
     *
     * @param pageable pagination and sorting information
     * @return a page of overdue tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getOverdueTasks(Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_OVERDUE_TASKS, pageable);
        return taskRepository.findOverdueTasks(LocalDateTime.now(), pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks with a specific priority level.
     *
     * @param priority the priority level
     * @param pageable pagination and sorting information
     * @return a page of tasks with the specified priority
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByPriority(String priority, Pageable pageable) {
        log.debug(ErrorMessages.FETCHING_TASKS_BY_PRIORITY, priority, pageable);
        Task.TaskPriority taskPriority = parsePriority(priority);
        return taskRepository.findByPriority(taskPriority, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Search for tasks by title or description.
     *
     * @param searchTerm the search term
     * @param pageable pagination and sorting information
     * @return a page of matching tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> searchTasks(String searchTerm, Pageable pageable) {
        log.debug(ErrorMessages.SEARCHING_TASKS, searchTerm, pageable);
        return taskRepository.searchTasks(searchTerm, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Mark a task as completed.
     *
     * @param id the task ID
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskResponseDTO markTaskAsCompleted(Long id) {
        log.info(ErrorMessages.MARKING_TASK_COMPLETE, id);

        Task task = findTaskByIdOrThrowNotFoundException(id);

        task.setIsCompleted(true);
        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info(ErrorMessages.TASK_MARKED_COMPLETE, id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Get statistics about tasks (e.g., count of completed tasks).
     *
     * @return task statistics
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        log.debug(ErrorMessages.FETCHING_STATISTICS);

        Long totalTasks = taskRepository.count();
        Long completedTasks = taskRepository.countByIsCompleted(true);
        Long pendingTasks = taskRepository.countByIsCompleted(false);

        return TaskStatistics.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .completionPercentage(calculateCompletionPercentage(completedTasks, totalTasks))
                .build();
    }

    // ==================== Helper Methods ====================

    /**
     * Find a task by ID or throw NotFoundException if not found.
     *
     * @param id the task ID
     * @return the task if found
     * @throws TaskNotFoundException if task does not exist
     */
    private Task findTaskByIdOrThrowNotFoundException(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.withTaskId(id));
    }

    /**
     * Validate task request data comprehensively.
     *
     * @param requestDTO the task request to validate
     * @throws TaskValidationException if validation fails
     */
    private void validateTaskRequestAndThrowIfInvalid(TaskRequestDTO requestDTO) {
        validateTaskTitle(requestDTO.getTitle());
        validateTaskDescription(requestDTO.getDescription());
        validateTaskAssignee(requestDTO.getAssignedTo());
        validateTaskDueDate(requestDTO.getDueDate());
    }

    /**
     * Validate task title.
     *
     * @param title the title to validate
     * @throws TaskValidationException if title is invalid
     */
    private void validateTaskTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new TaskValidationException(ErrorMessages.TASK_TITLE_REQUIRED);
        }

        if (title.length() > 100) {
            throw new TaskValidationException(ErrorMessages.TASK_TITLE_EXCEEDS_MAX_LENGTH);
        }

        if (title.length() < 1) {
            throw new TaskValidationException(ErrorMessages.TASK_TITLE_MIN_LENGTH);
        }
    }

    /**
     * Validate task description.
     *
     * @param description the description to validate
     * @throws TaskValidationException if description is invalid
     */
    private void validateTaskDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new TaskValidationException(ErrorMessages.TASK_DESCRIPTION_EXCEEDS_MAX_LENGTH);
        }
    }

    /**
     * Validate assigned to field.
     *
     * @param assignedTo the assigned to value to validate
     * @throws TaskValidationException if assignee is invalid
     */
    private void validateTaskAssignee(String assignedTo) {
        if (assignedTo != null && assignedTo.length() > 100) {
            throw new TaskValidationException(ErrorMessages.TASK_ASSIGNEE_EXCEEDS_MAX_LENGTH);
        }
    }

    /**
     * Validate task due date.
     *
     * @param dueDate the due date to validate
     */
    private void validateTaskDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            log.warn(ErrorMessages.WARNING_DUE_DATE_IN_PAST, dueDate);
        }
    }

    /**
     * Validate date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @throws TaskValidationException if date range is invalid
     */
    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new TaskValidationException(ErrorMessages.INVALID_DATE_RANGE);
        }
    }

    /**
     * Build a Task entity from a TaskRequestDTO.
     *
     * @param requestDTO the request DTO
     * @return a new Task entity
     */
    private Task buildTaskFromRequest(TaskRequestDTO requestDTO) {
        return Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .isCompleted(Optional.ofNullable(requestDTO.getIsCompleted()).orElse(false))
                .dueDate(requestDTO.getDueDate())
                .assignedTo(requestDTO.getAssignedTo())
                .priority(parsePriority(requestDTO.getPriority()))
                .build();
    }

    /**
     * Update all fields of a task from a TaskRequestDTO.
     *
     * @param task the task to update
     * @param requestDTO the request DTO with new values
     */
    private void updateTaskFromRequest(Task task, TaskRequestDTO requestDTO) {
        Optional.ofNullable(requestDTO.getTitle())
                .filter(title -> !title.isBlank())
                .ifPresent(task::setTitle);

        Optional.ofNullable(requestDTO.getDescription())
                .ifPresent(task::setDescription);

        Optional.ofNullable(requestDTO.getIsCompleted())
                .ifPresent(task::setIsCompleted);

        Optional.ofNullable(requestDTO.getDueDate())
                .ifPresent(task::setDueDate);

        Optional.ofNullable(requestDTO.getAssignedTo())
                .ifPresent(task::setAssignedTo);

        Optional.ofNullable(requestDTO.getPriority())
                .filter(priority -> !priority.isBlank())
                .map(this::parsePriority)
                .ifPresent(task::setPriority);
    }

    /**
     * Update only provided fields of a task from a TaskRequestDTO.
     *
     * @param task the task to update
     * @param requestDTO the request DTO with partial values
     */
    private void updateTaskFieldsIfPresent(Task task, TaskRequestDTO requestDTO) {
        updateTaskFromRequest(task, requestDTO);
    }

    /**
     * Convert a Task entity to a TaskResponseDTO.
     *
     * @param task the task entity
     * @return the task response DTO
     */
    private TaskResponseDTO convertToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isCompleted(task.getIsCompleted())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .modifiedAt(task.getModifiedAt())
                .assignedTo(task.getAssignedTo())
                .priority(task.getPriority().toString())
                .build();
    }

    /**
     * Parse priority string to TaskPriority enum.
     *
     * @param priority the priority string
     * @return the parsed TaskPriority enum value
     * @throws TaskValidationException if priority is invalid
     */
    private Task.TaskPriority parsePriority(String priority) {
        return Optional.ofNullable(priority)
                .map(String::toUpperCase)
                .map(this::getPriorityEnumOrThrow)
                .orElse(Task.TaskPriority.MEDIUM);
    }

    /**
     * Get TaskPriority enum from string or throw exception if invalid.
     *
     * @param priority the priority string (uppercase)
     * @return the TaskPriority enum
     * @throws TaskValidationException if priority is invalid
     */
    private Task.TaskPriority getPriorityEnumOrThrow(String priority) {
        try {
            return Task.TaskPriority.valueOf(priority);
        } catch (IllegalArgumentException e) {
            throw new TaskValidationException(ErrorMessages.INVALID_PRIORITY);
        }
    }

    /**
     * Calculate completion percentage.
     *
     * @param completedTasks the number of completed tasks
     * @param totalTasks the total number of tasks
     * @return the completion percentage
     */
    private Long calculateCompletionPercentage(Long completedTasks, Long totalTasks) {
        return totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;
    }
}

    /**
     * Create a new task.
     *
     * @param requestDTO the task data from the API request
     * @return the created task as a response DTO
     * @throws TaskValidationException if task data is invalid
     */
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        log.info("Creating new task with title: {}", requestDTO.getTitle());

        validateTaskRequest(requestDTO);

        Task task = Task.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .isCompleted(requestDTO.getIsCompleted() != null ? requestDTO.getIsCompleted() : false)
                .dueDate(requestDTO.getDueDate())
                .assignedTo(requestDTO.getAssignedTo())
                .priority(parsePriority(requestDTO.getPriority()))
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        return convertToResponseDTO(savedTask);
    }

    /**
     * Retrieve a task by its ID.
     *
     * @param id the task ID
     * @return the task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        log.debug("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.withTaskId(id));

        return convertToResponseDTO(task);
    }

    /**
     * Retrieve all tasks with pagination and sorting support.
     *
     * @param pageable pagination and sorting information
     * @return a page of tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        log.debug("Fetching all tasks with pageable: {}", pageable);
        return taskRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    /**
     * Update an existing task.
     *
     * @param id the task ID
     * @param requestDTO the updated task data
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     * @throws TaskValidationException if updated data is invalid
     */
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.withTaskId(id));

        validateTaskRequest(requestDTO);

        // Update only non-null fields
        if (requestDTO.getTitle() != null && !requestDTO.getTitle().isBlank()) {
            task.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            task.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getIsCompleted() != null) {
            task.setIsCompleted(requestDTO.getIsCompleted());
        }
        if (requestDTO.getDueDate() != null) {
            task.setDueDate(requestDTO.getDueDate());
        }
        if (requestDTO.getAssignedTo() != null) {
            task.setAssignedTo(requestDTO.getAssignedTo());
        }
        if (requestDTO.getPriority() != null && !requestDTO.getPriority().isBlank()) {
            task.setPriority(parsePriority(requestDTO.getPriority()));
        }

        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info("Task with id: {} updated successfully", id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Partially update a task (PATCH operation).
     *
     * @param id the task ID
     * @param requestDTO the partial task data
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskResponseDTO partialUpdateTask(Long id, TaskRequestDTO requestDTO) {
        log.info("Partially updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.withTaskId(id));

        // Update only provided fields
        if (requestDTO.getTitle() != null && !requestDTO.getTitle().isBlank()) {
            task.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            task.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getIsCompleted() != null) {
            task.setIsCompleted(requestDTO.getIsCompleted());
        }
        if (requestDTO.getDueDate() != null) {
            task.setDueDate(requestDTO.getDueDate());
        }
        if (requestDTO.getAssignedTo() != null) {
            task.setAssignedTo(requestDTO.getAssignedTo());
        }
        if (requestDTO.getPriority() != null && !requestDTO.getPriority().isBlank()) {
            task.setPriority(parsePriority(requestDTO.getPriority()));
        }

        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info("Task with id: {} partially updated successfully", id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Delete a task by its ID.
     *
     * @param id the task ID
     * @throws TaskNotFoundException if the task does not exist
     */
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);

        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException.withTaskId(id);
        }

        taskRepository.deleteById(id);
        log.info("Task with id: {} deleted successfully", id);
    }

    /**
     * Get all tasks filtered by completion status.
     *
     * @param isCompleted the completion status filter
     * @param pageable pagination and sorting information
     * @return a page of filtered tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByCompletionStatus(Boolean isCompleted, Pageable pageable) {
        log.debug("Fetching tasks with completion status: {} and pageable: {}", isCompleted, pageable);
        return taskRepository.findByIsCompleted(isCompleted, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks assigned to a specific person.
     *
     * @param assignedTo the person name
     * @param pageable pagination and sorting information
     * @return a page of tasks assigned to the specified person
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByAssignedTo(String assignedTo, Pageable pageable) {
        log.debug("Fetching tasks assigned to: {} with pageable: {}", assignedTo, pageable);
        return taskRepository.findByAssignedTo(assignedTo, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks with due dates in a specified range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @param pageable pagination and sorting information
     * @return a page of tasks with due dates in the specified range
     * @throws TaskValidationException if date range is invalid
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByDueDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Fetching tasks with due date range from {} to {} with pageable: {}", startDate, endDate, pageable);

        if (startDate.isAfter(endDate)) {
            throw new TaskValidationException("Start date must be before end date");
        }

        return taskRepository.findByDueDateRange(startDate, endDate, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all overdue tasks (incomplete tasks with past due dates).
     *
     * @param pageable pagination and sorting information
     * @return a page of overdue tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getOverdueTasks(Pageable pageable) {
        log.debug("Fetching overdue tasks with pageable: {}", pageable);
        return taskRepository.findOverdueTasks(LocalDateTime.now(), pageable).map(this::convertToResponseDTO);
    }

    /**
     * Get all tasks with a specific priority level.
     *
     * @param priority the priority level
     * @param pageable pagination and sorting information
     * @return a page of tasks with the specified priority
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> getTasksByPriority(String priority, Pageable pageable) {
        log.debug("Fetching tasks with priority: {} and pageable: {}", priority, pageable);
        Task.TaskPriority taskPriority = parsePriority(priority);
        return taskRepository.findByPriority(taskPriority, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Search for tasks by title or description.
     *
     * @param searchTerm the search term
     * @param pageable pagination and sorting information
     * @return a page of matching tasks
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> searchTasks(String searchTerm, Pageable pageable) {
        log.debug("Searching tasks with term: {} and pageable: {}", searchTerm, pageable);
        return taskRepository.searchTasks(searchTerm, pageable).map(this::convertToResponseDTO);
    }

    /**
     * Mark a task as completed.
     *
     * @param id the task ID
     * @return the updated task as a response DTO
     * @throws TaskNotFoundException if the task does not exist
     */
    public TaskResponseDTO markTaskAsCompleted(Long id) {
        log.info("Marking task with id: {} as completed", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> TaskNotFoundException.withTaskId(id));

        task.setIsCompleted(true);
        task.updateModifiedAt();
        Task updatedTask = taskRepository.save(task);

        log.info("Task with id: {} marked as completed", id);
        return convertToResponseDTO(updatedTask);
    }

    /**
     * Get statistics about tasks (e.g., count of completed tasks).
     *
     * @return task statistics
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        log.debug("Fetching task statistics");

        Long totalTasks = taskRepository.count();
        Long completedTasks = taskRepository.countByIsCompleted(true);
        Long pendingTasks = taskRepository.countByIsCompleted(false);

        return TaskStatistics.builder()
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .completionPercentage(totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0)
                .build();
    }

    // ==================== Helper Methods ====================

    /**
     * Validate task request data.
     *
     * @param requestDTO the task request to validate
     * @throws TaskValidationException if validation fails
     */
    private void validateTaskRequest(TaskRequestDTO requestDTO) {
        if (requestDTO.getTitle() == null || requestDTO.getTitle().isBlank()) {
            throw new TaskValidationException("Task title is required");
        }

        if (requestDTO.getTitle().length() > 100) {
            throw new TaskValidationException("Task title cannot exceed 100 characters");
        }

        if (requestDTO.getDueDate() != null && requestDTO.getDueDate().isBefore(LocalDateTime.now())) {
            log.warn("Task due date is in the past: {}", requestDTO.getDueDate());
        }
    }

    /**
     * Convert a Task entity to a TaskResponseDTO.
     *
     * @param task the task entity
     * @return the task response DTO
     */
    private TaskResponseDTO convertToResponseDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .isCompleted(task.getIsCompleted())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .modifiedAt(task.getModifiedAt())
                .assignedTo(task.getAssignedTo())
                .priority(task.getPriority().toString())
                .build();
    }

    /**
     * Parse priority string to TaskPriority enum.
     *
     * @param priority the priority string
     * @return the parsed TaskPriority enum value
     * @throws TaskValidationException if priority is invalid
     */
    private Task.TaskPriority parsePriority(String priority) {
        if (priority == null) {
            return Task.TaskPriority.MEDIUM;
        }

        try {
            return Task.TaskPriority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TaskValidationException("Invalid priority level. Allowed values: LOW, MEDIUM, HIGH, URGENT");
        }
    }
}

