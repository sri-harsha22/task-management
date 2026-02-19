import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html'
})
export class TaskDetailComponent implements OnInit {
  task?: Task;
  loading = false;
  error?: string;

  constructor(private route: ActivatedRoute, private service: TaskService, private router: Router) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id === 'new') return; // handled by form component
    if (id) this.load(+id);
  }

  load(id: number) {
    this.loading = true;
    this.service.getTask(id).subscribe({ next: t => { this.task = t; this.loading = false; }, error: () => { this.error = 'Failed to load task'; this.loading = false; } });
  }

  goEdit() {
    if (this.task) this.router.navigate(['/tasks', this.task.id, 'edit']);
  }
}
