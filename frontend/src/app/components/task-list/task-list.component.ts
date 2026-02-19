import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { Task, TaskPriority } from '../../models/task.model';
import { TaskService, PagedResponse, TaskFilterCriteria } from '../../services/task.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CONFIG } from '../../config/app.config';

/**
 * TaskListComponent - Manages display and filtering of tasks
 *
 * Features:
 * - Task CRUD operations
 * - Pagination with UI controls
 * - Filtering by status, priority, assignment
 * - Search functionality
 * - Form validation feedback
 * - Empty state display
 * - Proper memory management (unsubscribe on destroy)
 * - Type-safe operations
 */
@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent implements OnInit, OnDestroy {
  // Data
  tasks: Task[] = [];
  filteredTasks: Task[] = [];

  // Pagination
  currentPage = 0;
  pageSize = CONFIG.DEFAULT_PAGE_SIZE;
  totalElements = 0;
  totalPages = 0;

  // Filters
  selectedStatus: boolean | null = null;
  selectedPriority: TaskPriority | null = null;
  selectedAssignee: string = '';
  searchTerm: string = '';

  // UI State
  loading = false;
  /** @used-in-template */
  searching = false;
  error: string | null = null;
  successMessage: string | null = null;

  // Pagination options
  /** @used-in-template */
  pageSizeOptions = CONFIG.PAGE_SIZE_OPTIONS;

  // Priority options for dropdown
  /** @used-in-template */
  priorityOptions: TaskPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

  // Memory management
  private destroy$ = new Subject<void>();

  constructor(private taskService: TaskService, private router: Router) {}

  /** @used-in-template */
  ngOnInit(): void {
    this.loadTasks();
  }

  /** @lifecycle-hook */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load tasks with current filters and pagination
   */
  loadTasks(): void {
    this.loading = true;
    this.error = null;

    const criteria = this.buildFilterCriteria();

    this.taskService
      .getTasks(this.currentPage, this.pageSize, criteria)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: PagedResponse<Task>) => {
          this.tasks = response.content || [];
          this.filteredTasks = this.tasks;
          this.totalElements = response.totalElements || 0;
          this.totalPages = response.totalPages || 0;
          this.loading = false;

          if (this.tasks.length === 0 && !this.isFiltering()) {
            this.successMessage = CONFIG.MESSAGES.NO_TASKS;
          }
        },
        error: (error) => {
          console.error('Error loading tasks:', error);
          this.error = CONFIG.MESSAGES.LOAD_ERROR;
          this.loading = false;
        }
      });
  }

  /**
   * Build filter criteria from current filter selections
   */
  private buildFilterCriteria(): TaskFilterCriteria {
    return {
      status: this.selectedStatus,
      priority: this.selectedPriority,
      assignedTo: this.selectedAssignee || undefined,
      searchTerm: this.searchTerm || undefined
    };
  }

  /**
   * Check if any filters are active
   */
  private isFiltering(): boolean {
    return (
      this.selectedStatus !== null ||
      this.selectedPriority !== null ||
      this.selectedAssignee !== '' ||
      this.searchTerm !== ''
    );
  }

  /**
   * Search tasks by term
   */
  searchTasks(): void {
    this.currentPage = 0;
    this.loadTasks();
  }

  /**
   * Clear all filters
   */
  clearFilters(): void {
    this.selectedStatus = null;
    this.selectedPriority = null;
    this.selectedAssignee = '';
    this.searchTerm = '';
    this.currentPage = 0;
    this.error = null;
    this.loadTasks();
  }

  /**
   * Filter by status
   */
  filterByStatus(completed: boolean | null): void {
    this.selectedStatus = completed;
    this.currentPage = 0;
    this.loadTasks();
  }

  /**
   * Filter by priority
   */
  filterByPriority(priority: TaskPriority | null): void {
    this.selectedPriority = priority;
    this.currentPage = 0;
    this.loadTasks();
  }

  /**
   * Filter by assignee
   */
  filterByAssignee(assignee: string): void {
    this.selectedAssignee = assignee;
    this.currentPage = 0;
    this.loadTasks();
  }

  /**
   * Toggle task completion status
   */
  toggleComplete(task: Task): void {
    if (!task.id) {
      this.error = CONFIG.MESSAGES.INVALID_TASK;
      return;
    }

    this.taskService
      .markCompleted(task.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedTask: Task) => {
          this.successMessage = CONFIG.MESSAGES.STATUS_UPDATED;
          this.loadTasks();
          this.clearMessage();
        },
        error: (error) => {
          console.error('Error updating task status:', error);
          this.error = CONFIG.MESSAGES.STATUS_UPDATE_ERROR;
        }
      });
  }

  /**
   * Navigate to edit task
   */
  edit(task: Task): void {
    if (!task.id) {
      this.error = CONFIG.MESSAGES.INVALID_TASK;
      return;
    }
    this.router.navigate(['/tasks', task.id]);
  }

  /**
   * Navigate to create new task
   */
  create(): void {
    this.router.navigate(['/tasks/new']);
  }

  /**
   * Delete task with confirmation
   */
  delete(task: Task): void {
    if (!task.id) {
      this.error = CONFIG.MESSAGES.INVALID_TASK;
      return;
    }

    if (!confirm(CONFIG.MESSAGES.CONFIRM_DELETE)) {
      return;
    }

    this.taskService
      .deleteTask(task.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = CONFIG.MESSAGES.DELETE_SUCCESS;
          this.loadTasks();
          this.clearMessage();
        },
        error: (error) => {
          console.error('Error deleting task:', error);
          this.error = CONFIG.MESSAGES.DELETE_ERROR;
        }
      });
  }

  /**
   * Go to next page
   */
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTasks();
    }
  }

  /**
   * Go to previous page
   */
  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTasks();
    }
  }

  /**
   * Go to specific page
   */
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadTasks();
    }
  }

  /**
   * Change page size
   */
  changePageSize(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0;
    this.loadTasks();
  }

  /**
   * Clear success/error messages after delay
   */
  private clearMessage(): void {
    setTimeout(() => {
      this.successMessage = null;
    }, CONFIG.MESSAGE_DISPLAY_TIME);
  }

  /**
   * Check if task is overdue
   */
  isOverdue(task: Task): boolean {
    if (!task.dueDate || task.isCompleted) {
      return false;
    }
    return new Date(task.dueDate) < new Date();
  }

  /**
   * Get CSS class for task row
   */
  getTaskRowClass(task: Task): string {
    if (task.isCompleted) {
      return 'task-row-completed';
    }
    if (this.isOverdue(task)) {
      return 'task-row-overdue';
    }
    return '';
  }

  /**
   * Get priority badge class
   */
  getPriorityClass(priority?: TaskPriority): string {
    return `priority-${priority?.toLowerCase() || 'medium'}`;
  }

  /**
   * Get pagination range for display
   */
  getPaginationRange(): number[] {
    const maxPages = 5; // Show max 5 page buttons
    const pages: number[] = [];

    if (this.totalPages <= maxPages) {
      for (let i = 0; i < this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Show first page, last page, and pages around current
      const start = Math.max(0, this.currentPage - 2);
      const end = Math.min(this.totalPages - 1, this.currentPage + 2);

      if (start > 0) pages.push(0);
      if (start > 1) pages.push(-1); // Ellipsis placeholder

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (end < this.totalPages - 2) pages.push(-1); // Ellipsis placeholder
      if (end < this.totalPages - 1) pages.push(this.totalPages - 1);
    }

    return pages;
  }

  /**
   * TrackBy function for *ngFor optimization
   */
  trackByTaskId(index: number, task: Task): number | undefined {
    return task.id;
  }
}
