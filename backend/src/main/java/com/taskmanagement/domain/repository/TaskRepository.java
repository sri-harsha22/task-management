package com.taskmanagement.domain.repository;

import com.taskmanagement.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entity.
 * Provides database access and querying capabilities for tasks.
 *
 * Extends JpaRepository to inherit standard CRUD operations and pagination support.
 * Includes custom query methods for advanced filtering and searching scenarios.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks with a specific completion status.
     *
     * @param isCompleted the completion status to filter by
     * @param pageable pagination information
     * @return a page of tasks matching the completion status
     */
    Page<Task> findByIsCompleted(Boolean isCompleted, Pageable pageable);

    /**
     * Find all tasks assigned to a specific person.
     *
     * @param assignedTo the name of the person to filter by
     * @param pageable pagination information
     * @return a page of tasks assigned to the specified person
     */
    Page<Task> findByAssignedTo(String assignedTo, Pageable pageable);

    /**
     * Find all tasks with a due date within a specified range.
     *
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @param pageable pagination information
     * @return a page of tasks with due dates within the range
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC")
    Page<Task> findByDueDateRange(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    /**
     * Find all incomplete tasks with due dates in the past.
     * Useful for identifying overdue tasks.
     *
     * @param currentDate the current date and time
     * @param pageable pagination information
     * @return a page of overdue incomplete tasks
     */
    @Query("SELECT t FROM Task t WHERE t.isCompleted = false AND t.dueDate < :currentDate ORDER BY t.dueDate ASC")
    Page<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    /**
     * Find all tasks with a specific priority level.
     *
     * @param priority the priority level to filter by
     * @param pageable pagination information
     * @return a page of tasks with the specified priority
     */
    Page<Task> findByPriority(Task.TaskPriority priority, Pageable pageable);

    /**
     * Search for tasks by title or description containing the search term.
     *
     * @param searchTerm the search term to look for
     * @param pageable pagination information
     * @return a page of tasks matching the search term
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Task> searchTasks(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count the number of completed tasks.
     *
     * @return the count of completed tasks
     */
    Long countByIsCompleted(Boolean isCompleted);
}

