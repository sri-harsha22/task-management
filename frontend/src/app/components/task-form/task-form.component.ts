import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html'
})
export class TaskFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  id?: number;
  error?: string;

  constructor(private fb: FormBuilder, private svc: TaskService, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: [''],
      dueDate: [''],
      assignedTo: [''],
      priority: ['MEDIUM'],
      isCompleted: [false]
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'new') {
      this.isEdit = true;
      this.id = +idParam;
      this.svc.getTask(this.id).subscribe({ next: t => this.form.patchValue(t), error: () => this.error = 'Failed to load task' });
    }
  }

  submit() {
    if (this.form.invalid) return;
    const payload: Partial<Task> = this.form.value;
    if (this.isEdit && this.id) {
      this.svc.updateTask(this.id, payload).subscribe({ next: () => this.router.navigate(['/']), error: () => this.error = 'Failed to update' });
    } else {
      this.svc.createTask(payload).subscribe({ next: () => this.router.navigate(['/']), error: () => this.error = 'Failed to create' });
    }
  }
}
