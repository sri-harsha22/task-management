/**
 * Application Configuration
 *
 * Centralized configuration for:
 * - API settings
 * - UI constants
 * - Message strings
 * - Default values
 */

export const CONFIG = {
  // API Configuration
  API: {
    BASE_URL: '/api',
    TIMEOUT: 30000,
  },

  // Pagination Configuration
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [5, 10, 20, 50],
  MAX_PAGE_SIZE: 100,

  // Search Configuration
  SEARCH_DEBOUNCE_TIME: 300,
  MIN_SEARCH_LENGTH: 1,

  // Message Display
  MESSAGE_DISPLAY_TIME: 3000, // milliseconds

  // Messages
  MESSAGES: {
    // Success Messages
    CREATE_SUCCESS: 'Task created successfully',
    UPDATE_SUCCESS: 'Task updated successfully',
    DELETE_SUCCESS: 'Task deleted successfully',
    STATUS_UPDATED: 'Task status updated',
    SEARCH_SUCCESS: 'Search completed',

    // Error Messages
    CREATE_ERROR: 'Failed to create task',
    UPDATE_ERROR: 'Failed to update task',
    DELETE_ERROR: 'Failed to delete task',
    STATUS_UPDATE_ERROR: 'Failed to update task status',
    LOAD_ERROR: 'Failed to load tasks',
    SEARCH_ERROR: 'Failed to search tasks',
    INVALID_TASK: 'Invalid task data',
    INVALID_INPUT: 'Please check your input and try again',

    // Info Messages
    NO_TASKS: 'No tasks found',
    NO_RESULTS: 'No results found for your search',
    LOADING: 'Loading tasks...',
    SEARCHING: 'Searching...',

    // Confirmation Messages
    CONFIRM_DELETE: 'Are you sure you want to delete this task?',
    CONFIRM_ACTION: 'Are you sure?',

    // Validation Messages
    REQUIRED_FIELD: 'This field is required',
    MIN_LENGTH: (min: number) => `Minimum length is ${min} characters`,
    MAX_LENGTH: (max: number) => `Maximum length is ${max} characters`,
    INVALID_EMAIL: 'Invalid email address',
    INVALID_DATE: 'Invalid date',
  },

  // Form Configuration
  FORM: {
    TITLE_MIN_LENGTH: 1,
    TITLE_MAX_LENGTH: 100,
    DESCRIPTION_MAX_LENGTH: 1000,
    ASSIGNEE_MAX_LENGTH: 100,
  },

  // Priority Levels
  PRIORITY_LEVELS: ['LOW', 'MEDIUM', 'HIGH', 'URGENT'] as const,

  // Default Sort
  DEFAULT_SORT: 'id,asc',

  // Feature Flags
  FEATURES: {
    ENABLE_OFFLINE_MODE: false,
    ENABLE_ADVANCED_FILTERS: true,
    ENABLE_BULK_OPERATIONS: false,
  },

  // Animation
  ANIMATION_DURATION: 300, // milliseconds

  // Validation Rules
  VALIDATION: {
    TITLE_REQUIRED: true,
    TITLE_MIN_LENGTH: 1,
    TITLE_MAX_LENGTH: 100,
    DESCRIPTION_MAX_LENGTH: 1000,
  },

  // API Endpoints
  ENDPOINTS: {
    TASKS: '/tasks',
    TASK_BY_ID: (id: number) => `/tasks/${id}`,
    TASK_COMPLETE: (id: number) => `/tasks/${id}/complete`,
    TASK_SEARCH: '/tasks/search',
    TASK_FILTER: (type: string) => `/tasks/filter/${type}`,
    TASK_STATISTICS: '/tasks/statistics',
  },

  // Cache Configuration
  CACHE: {
    ENABLE_CACHING: true,
    CACHE_DURATION: 5 * 60 * 1000, // 5 minutes
  },

  // Retry Configuration
  RETRY: {
    MAX_RETRIES: 3,
    RETRY_DELAY: 1000, // milliseconds
    BACKOFF_MULTIPLIER: 2,
  },
};

/**
 * Type-safe access to configuration
 */
export type AppConfig = typeof CONFIG;

