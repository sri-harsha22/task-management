import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CONFIG } from '../../config/app.config';

/**
 * TaskDetailComponent - Displays detailed information about a single task
 *
 * Features:
 * - View task details
 * - Navigate to edit mode
 * - Proper memory management
 * - Error handling
 */
@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html'
})
export class TaskDetailComponent implements OnInit, OnDestroy {
  task?: Task;
  loading = false;
  error?: string;

  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id === 'new') {
      return; // Handled by form component
    }
    if (id) {
      this.loadTask(+id);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadTask(id: number): void {
    this.loading = true;
    this.error = undefined;

    this.taskService
      .getTask(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: Task) => {
          this.task = task;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading task:', error);
          this.error = CONFIG.MESSAGES.LOAD_ERROR;
          this.loading = false;
        }
      });
  }

  goEdit(): void {
    if (this.task?.id) {
      this.router.navigate(['/tasks', this.task.id, 'edit']);
    }
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }
}
