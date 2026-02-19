import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CONFIG } from '../../config/app.config';

/**
 * TaskFormComponent - Handles task creation and editing
 *
 * Features:
 * - Create new tasks
 * - Edit existing tasks
 * - Form validation
 * - Proper memory management
 * - Error handling
 */
@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.scss']
})
export class TaskFormComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  isEdit = false;
  taskId?: number;
  error?: string;
  loading = false;
  submitting = false;

  private destroy$ = new Subject<void>();

  constructor(
    private formBuilder: FormBuilder,
    private taskService: TaskService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.checkEditMode();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialize the task form with validators
   */
  private initializeForm(): void {
    this.form = this.formBuilder.group({
      title: [
        '',
        [
          Validators.required,
          Validators.minLength(CONFIG.FORM.TITLE_MIN_LENGTH),
          Validators.maxLength(CONFIG.FORM.TITLE_MAX_LENGTH)
        ]
      ],
      description: [
        '',
        [Validators.maxLength(CONFIG.FORM.DESCRIPTION_MAX_LENGTH)]
      ],
      dueDate: [''],
      assignedTo: [
        '',
        [Validators.maxLength(CONFIG.FORM.ASSIGNEE_MAX_LENGTH)]
      ],
      priority: ['MEDIUM'],
      isCompleted: [false]
    });
  }

  /**
   * Check if we're in edit mode and load task if needed
   */
  private checkEditMode(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam && idParam !== 'new') {
      this.isEdit = true;
      this.taskId = +idParam;
      this.loadTask(this.taskId);
    }
  }

  /**
   * Load existing task for editing
   */
  private loadTask(id: number): void {
    this.loading = true;
    this.error = undefined;

    this.taskService
      .getTask(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: Task) => {
          this.form.patchValue(task);
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading task:', error);
          this.error = CONFIG.MESSAGES.LOAD_ERROR;
          this.loading = false;
        }
      });
  }

  /**
   * Submit the form (create or update)
   */
  submit(): void {
    if (this.form.invalid) {
      this.markFormGroupTouched(this.form);
      this.error = CONFIG.MESSAGES.INVALID_INPUT;
      return;
    }

    const payload: Partial<Task> = this.form.value;
    this.submitting = true;
    this.error = undefined;

    if (this.isEdit && this.taskId) {
      this.updateTask(this.taskId, payload);
    } else {
      this.createTask(payload);
    }
  }

  /**
   * Create a new task
   */
  private createTask(payload: Partial<Task>): void {
    this.taskService
      .createTask(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
        },
        error: (error) => {
          console.error('Error creating task:', error);
          this.error = CONFIG.MESSAGES.CREATE_ERROR;
          this.submitting = false;
        }
      });
  }

  /**
   * Update an existing task
   */
  private updateTask(id: number, payload: Partial<Task>): void {
    this.taskService
      .updateTask(id, payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.router.navigate(['/tasks']);
        },
        error: (error) => {
          console.error('Error updating task:', error);
          this.error = CONFIG.MESSAGES.UPDATE_ERROR;
          this.submitting = false;
        }
      });
  }

  /**
   * Cancel form and navigate back
   */
  cancel(): void {
    this.router.navigate(['/tasks']);
  }

  /**
   * Mark all form controls as touched to show validation errors
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  /**
   * Check if a form control has errors and is touched
   */
  hasError(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  /**
   * Get error message for a form control
   */
  getErrorMessage(controlName: string): string {
    const control = this.form.get(controlName);

    if (!control) {
      return '';
    }

    if (control.hasError('required')) {
      return CONFIG.MESSAGES.REQUIRED_FIELD;
    }

    if (control.hasError('minlength')) {
      const minLength = control.getError('minlength').requiredLength;
      return CONFIG.MESSAGES.MIN_LENGTH(minLength);
    }

    if (control.hasError('maxlength')) {
      const maxLength = control.getError('maxlength').requiredLength;
      return CONFIG.MESSAGES.MAX_LENGTH(maxLength);
    }

    return '';
  }
}
