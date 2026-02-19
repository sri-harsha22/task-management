/**
 * Task Priority enumeration
 */
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

/**
 * Task interface
 *
 * Represents a task in the system with all properties
 * aligned with the backend entity model
 */
export interface Task {
  id?: number;
  title: string;
  description?: string;
  isCompleted?: boolean;
  dueDate?: string; // ISO string format
  createdAt?: string;
  modifiedAt?: string;
  assignedTo?: string;
  priority?: TaskPriority;
}

/**
 * Create task request
 */
export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
  assignedTo?: string;
  priority?: TaskPriority;
}

/**
 * Update task request
 */
export interface UpdateTaskRequest extends Partial<Task> {
  id: number;
}

/**
 * Task filter criteria
 */
export interface TaskFilter {
  status?: boolean | null;
  priority?: TaskPriority | null;
  assignedTo?: string;
  searchTerm?: string;
  dueDateStart?: string;
  dueDateEnd?: string;
}
