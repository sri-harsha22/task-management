package com.taskmanagement.service;

import com.taskmanagement.domain.entity.Task;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.dto.TaskRequestDTO;
import com.taskmanagement.dto.TaskResponseDTO;
import com.taskmanagement.exception.TaskNotFoundException;
import com.taskmanagement.exception.TaskValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService class.
 *
 * This test class demonstrates comprehensive test coverage for the service layer
 * using JUnit 5 and Mockito for dependency mocking.
 *
 * Test categories covered:
 * - CRUD operations (Create, Read, Update, Delete)
 * - Filtering and querying operations
 * - Error handling and validation
 * - Statistics calculation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskRequestDTO testRequestDTO;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testPageable = PageRequest.of(0, 10);

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .isCompleted(false)
                .dueDate(LocalDateTime.now().plusDays(1))
                .priority(Task.TaskPriority.HIGH)
                .assignedTo("John Doe")
                .build();

        testRequestDTO = TaskRequestDTO.builder()
                .title("Test Task")
                .description("Test Description")
                .isCompleted(false)
                .dueDate(LocalDateTime.now().plusDays(1))
                .priority("HIGH")
                .assignedTo("John Doe")
                .build();
    }

    // ==================== Create Operation Tests ====================

    @Test
    @DisplayName("Should create task successfully with valid data")
    void testCreateTaskSuccess() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponseDTO result = taskService.createTask(testRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testTask.getId());
        assertThat(result.getTitle()).isEqualTo(testTask.getTitle());
        assertThat(result.getDescription()).isEqualTo(testTask.getDescription());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when title is blank")
    void testCreateTaskWithBlankTitle() {
        // Given
        testRequestDTO.setTitle("");

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(testRequestDTO))
                .isInstanceOf(TaskValidationException.class)
                .hasMessageContaining("Task title is required");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when title exceeds max length")
    void testCreateTaskWithTitleTooLong() {
        // Given
        testRequestDTO.setTitle("a".repeat(101));

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(testRequestDTO))
                .isInstanceOf(TaskValidationException.class)
                .hasMessageContaining("cannot exceed 100 characters");

        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== Read Operation Tests ====================

    @Test
    @DisplayName("Should retrieve task by ID successfully")
    void testGetTaskByIdSuccess() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskResponseDTO result = taskService.getTaskById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testTask.getId());
        assertThat(result.getTitle()).isEqualTo(testTask.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when task not found by ID")
    void testGetTaskByIdNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should retrieve all tasks with pagination")
    void testGetAllTasksSuccess() {
        // Given
        List<Task> taskList = List.of(testTask);
        Page<Task> expectedPage = new PageImpl<>(taskList, testPageable, 1);
        when(taskRepository.findAll(testPageable)).thenReturn(expectedPage);

        // When
        Page<TaskResponseDTO> result = taskService.getAllTasks(testPageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getId()).isEqualTo(testTask.getId());
        verify(taskRepository, times(1)).findAll(testPageable);
    }

    // ==================== Update Operation Tests ====================

    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTaskSuccess() {
        // Given
        testRequestDTO.setTitle("Updated Title");
        Task updatedTask = testTask;
        updatedTask.setTitle("Updated Title");
        updatedTask.setModifiedAt(LocalDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // When
        TaskResponseDTO result = taskService.updateTask(1L, testRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void testUpdateTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(999L, testRequestDTO))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== Delete Operation Tests ====================

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTaskSuccess() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void testDeleteTaskNotFound() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(any());
    }

    // ==================== Filtering Tests ====================

    @Test
    @DisplayName("Should retrieve tasks by completion status")
    void testGetTasksByCompletionStatusSuccess() {
        // Given
        List<Task> taskList = List.of(testTask);
        Page<Task> expectedPage = new PageImpl<>(taskList, testPageable, 1);
        when(taskRepository.findByIsCompleted(false, testPageable)).thenReturn(expectedPage);

        // When
        Page<TaskResponseDTO> result = taskService.getTasksByCompletionStatus(false, testPageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(taskRepository, times(1)).findByIsCompleted(false, testPageable);
    }

    @Test
    @DisplayName("Should mark task as completed")
    void testMarkTaskAsCompletedSuccess() {
        // Given
        Task completedTask = testTask;
        completedTask.setIsCompleted(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        // When
        TaskResponseDTO result = taskService.markTaskAsCompleted(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIsCompleted()).isTrue();
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ==================== Statistics Tests ====================

    @Test
    @DisplayName("Should retrieve task statistics successfully")
    void testGetTaskStatisticsSuccess() {
        // Given
        when(taskRepository.count()).thenReturn(10L);
        when(taskRepository.countByIsCompleted(true)).thenReturn(6L);
        when(taskRepository.countByIsCompleted(false)).thenReturn(4L);

        // When
        TaskStatistics result = taskService.getTaskStatistics();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalTasks()).isEqualTo(10L);
        assertThat(result.getCompletedTasks()).isEqualTo(6L);
        assertThat(result.getPendingTasks()).isEqualTo(4L);
        assertThat(result.getCompletionPercentage()).isEqualTo(60L);
        verify(taskRepository, times(1)).count();
        verify(taskRepository, times(2)).countByIsCompleted(any());
    }

    @Test
    @DisplayName("Should handle zero tasks in statistics")
    void testGetTaskStatisticsWithZeroTasks() {
        // Given
        when(taskRepository.count()).thenReturn(0L);
        when(taskRepository.countByIsCompleted(true)).thenReturn(0L);
        when(taskRepository.countByIsCompleted(false)).thenReturn(0L);

        // When
        TaskStatistics result = taskService.getTaskStatistics();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalTasks()).isEqualTo(0L);
        assertThat(result.getCompletionPercentage()).isEqualTo(0L);
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Should throw exception for invalid priority")
    void testInvalidPriority() {
        // Given
        testRequestDTO.setPriority("INVALID");

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(testRequestDTO))
                .isInstanceOf(TaskValidationException.class)
                .hasMessageContaining("Invalid priority level");
    }

    @Test
    @DisplayName("Should throw exception for invalid date range")
    void testInvalidDateRange() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().plusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // When & Then
        assertThatThrownBy(() -> taskService.getTasksByDueDateRange(startDate, endDate, testPageable))
                .isInstanceOf(TaskValidationException.class)
                .hasMessageContaining("Start date must be before end date");
    }
}

