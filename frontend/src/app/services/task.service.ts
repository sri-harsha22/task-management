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
   */
  getTasks(
    page: number = 0,
    size: number = 10,
    filters?: TaskFilterCriteria,
    sort: string = 'id,asc'
  ): Observable<PagedResponse<Task>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', sort);

    // Add filter parameters
    if (filters) {
      if (filters.status !== null && filters.status !== undefined) {
        params = params.set('isCompleted', String(filters.status));
      }
      if (filters.priority) {
        params = params.set('priority', filters.priority);
      }
      if (filters.assignedTo) {
        params = params.set('assignedTo', filters.assignedTo);
      }
      if (filters.searchTerm) {
        params = params.set('searchTerm', filters.searchTerm);
      }
      if (filters.dueDateStart) {
        params = params.set('dueDateStart', filters.dueDateStart);
      }
      if (filters.dueDateEnd) {
        params = params.set('dueDateEnd', filters.dueDateEnd);
      }
    }

    return this.http.get<PagedResponse<Task>>(this.baseUrl, { params });
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
   * Search tasks by term
   */
  searchTasks(searchTerm: string, page: number = 0, size: number = 10): Observable<PagedResponse<Task>> {
    return this.getTasks(page, size, { searchTerm });
  }

  /**
   * Get overdue tasks
   */
  getOverdueTasks(page: number = 0, size: number = 10): Observable<PagedResponse<Task>> {
    return this.getTasks(page, size, { status: false });
  }
}
