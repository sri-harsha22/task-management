import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskPriority } from '../models/task.model';
import { environment } from '../../environments/environment';

/**
 * Paginated response from backend API
 */
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * Filter criteria for task queries
 */
export interface TaskFilterCriteria {
  status?: boolean | null;
  priority?: TaskPriority | null;
  assignedTo?: string;
  searchTerm?: string;
  dueDateStart?: string;
  dueDateEnd?: string;
}

/**
 * TaskService - Handles all API communication for tasks
 *
 * Provides type-safe methods for:
 * - CRUD operations
 * - Filtering and searching
 * - Pagination and sorting
 * - Status updates
 */
@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly baseUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  /**
   * Get paginated list of tasks with optional filtering
   *
   * This method intelligently routes to the appropriate backend endpoint
   * based on the filter criteria provided.
   */
  getTasks(
    page: number = 0,
    size: number = 10,
    filters?: TaskFilterCriteria,
    sort: string = 'id,asc'
  ): Observable<PagedResponse<Task>> {
    // If filters are provided, route to appropriate filter endpoint
    if (filters) {
      // Search takes priority
      if (filters.searchTerm) {
        return this.searchTasks(filters.searchTerm, page, size);
      }

      // Status filter
      if (filters.status !== null && filters.status !== undefined) {
        return this.filterByCompletionStatus(filters.status, page, size);
      }

      // Priority filter
      if (filters.priority) {
        return this.filterByPriority(filters.priority, page, size);
      }

      // Assignee filter
      if (filters.assignedTo) {
        return this.filterByAssignee(filters.assignedTo, page, size);
      }

      // Date range filter
      if (filters.dueDateStart && filters.dueDateEnd) {
        return this.filterByDateRange(filters.dueDateStart, filters.dueDateEnd, page, size);
      }
    }

    // No filters - use base endpoint
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', sort);

    return this.http.get<PagedResponse<Task>>(this.baseUrl, { params });
  }

  /**
   * Filter tasks by completion status
   */
  filterByCompletionStatus(
    isCompleted: boolean,
    page: number = 0,
    size: number = 10
  ): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('isCompleted', String(isCompleted))
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/filter/completed`, { params });
  }

  /**
   * Filter tasks by priority
   */
  filterByPriority(
    priority: TaskPriority,
    page: number = 0,
    size: number = 10
  ): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('priority', priority)
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/filter/priority`, { params });
  }

  /**
   * Filter tasks by assignee
   */
  filterByAssignee(
    assignedTo: string,
    page: number = 0,
    size: number = 10
  ): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('assignedTo', assignedTo)
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/filter/assigned-to`, { params });
  }

  /**
   * Filter tasks by date range
   */
  filterByDateRange(
    startDate: string,
    endDate: string,
    page: number = 0,
    size: number = 10
  ): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/filter/due-date-range`, { params });
  }

  /**
   * Get overdue tasks
   */
  getOverdueTasks(page: number = 0, size: number = 10): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/filter/overdue`, { params });
  }

  /**
   * Search tasks by term
   */
  searchTasks(searchTerm: string, page: number = 0, size: number = 10): Observable<PagedResponse<Task>> {
    const params = new HttpParams()
      .set('searchTerm', searchTerm)
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PagedResponse<Task>>(`${this.baseUrl}/search`, { params });
  }

  /**
   * Get task by ID
   */
  getTask(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.baseUrl}/${id}`);
  }

  /**
   * Create new task
   */
  createTask(payload: Partial<Task>): Observable<Task> {
    return this.http.post<Task>(this.baseUrl, payload);
  }

  /**
   * Update entire task
   */
  updateTask(id: number, payload: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${id}`, payload);
  }

  /**
   * Partially update task
   */
  patchTask(id: number, payload: Partial<Task>): Observable<Task> {
    return this.http.patch<Task>(`${this.baseUrl}/${id}`, payload);
  }

  /**
   * Delete task
   */
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mark task as completed
   */
  markCompleted(id: number): Observable<Task> {
    return this.http.put<Task>(`${this.baseUrl}/${id}/complete`, {});
  }

  /**
   * Get task statistics
   */
  getStatistics(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/statistics`);
  }
}
