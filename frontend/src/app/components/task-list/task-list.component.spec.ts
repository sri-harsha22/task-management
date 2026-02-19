import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { TaskService, PagedResponse } from '../../services/task.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Task } from '../../models/task.model';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;
  let router: jasmine.SpyObj<Router>;

  const mockTasks: Task[] = [
    { id: 1, title: 'Task 1', isCompleted: false, priority: 'HIGH' },
    { id: 2, title: 'Task 2', isCompleted: true, priority: 'LOW' }
  ];

  const mockPagedResponse: PagedResponse<Task> = {
    content: mockTasks,
    totalElements: 2,
    totalPages: 1,
    number: 0,
    size: 10,
    first: true,
    last: true
  };

  beforeEach(async () => {
    const taskServiceSpy = jasmine.createSpyObj('TaskService', [
      'getTasks',
      'getTask',
      'createTask',
      'updateTask',
      'deleteTask',
      'markCompleted',
      'searchTasks'
    ]);

    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [TaskListComponent],
      providers: [
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    fixture.destroy();
  });

  describe('ngOnInit', () => {
    it('should load tasks on initialization', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      fixture.detectChanges();

      expect(taskService.getTasks).toHaveBeenCalledWith(0, 10, jasmine.any(Object));
      expect(component.tasks.length).toBe(2);
    });

    it('should handle loading error', () => {
      taskService.getTasks.and.returnValue(throwError(() => new Error('API Error')));

      fixture.detectChanges();

      expect(component.error).toContain('Failed');
      expect(component.loading).toBe(false);
    });
  });

  describe('loadTasks', () => {
    it('should set loading state during fetch', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      component.loadTasks();

      expect(component.loading).toBe(false);
      expect(component.tasks.length).toBe(2);
    });

    it('should update pagination info', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      component.loadTasks();

      expect(component.totalElements).toBe(2);
      expect(component.totalPages).toBe(1);
    });
  });

  describe('filtering', () => {
    beforeEach(() => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));
    });

    it('should filter by status', () => {
      component.filterByStatus(false);

      expect(component.selectedStatus).toBe(false);
      expect(component.currentPage).toBe(0);
      expect(taskService.getTasks).toHaveBeenCalled();
    });

    it('should filter by priority', () => {
      component.filterByPriority('HIGH');

      expect(component.selectedPriority).toBe('HIGH');
      expect(taskService.getTasks).toHaveBeenCalled();
    });

    it('should filter by assignee', () => {
      component.filterByAssignee('John');

      expect(component.selectedAssignee).toBe('John');
      expect(taskService.getTasks).toHaveBeenCalled();
    });

    it('should clear all filters', () => {
      component.selectedStatus = false;
      component.selectedPriority = 'HIGH';
      component.selectedAssignee = 'John';
      component.searchTerm = 'test';

      component.clearFilters();

      expect(component.selectedStatus).toBeNull();
      expect(component.selectedPriority).toBeNull();
      expect(component.selectedAssignee).toBe('');
      expect(component.searchTerm).toBe('');
    });
  });

  describe('task operations', () => {
    beforeEach(() => {
      component.tasks = mockTasks;
    });

    it('should toggle task completion', () => {
      taskService.markCompleted.and.returnValue(of({
        ...mockTasks[0],
        isCompleted: true
      }));
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      component.toggleComplete(mockTasks[0]);

      expect(taskService.markCompleted).toHaveBeenCalledWith(1);
    });

    it('should handle error when toggling completion', () => {
      taskService.markCompleted.and.returnValue(throwError(() => new Error('API Error')));

      component.toggleComplete(mockTasks[0]);

      expect(component.error).toContain('Failed');
    });

    it('should delete task with confirmation', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      taskService.deleteTask.and.returnValue(of(void 0));
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      component.delete(mockTasks[0]);

      expect(taskService.deleteTask).toHaveBeenCalledWith(1);
    });

    it('should not delete task if confirmation cancelled', () => {
      spyOn(window, 'confirm').and.returnValue(false);

      component.delete(mockTasks[0]);

      expect(taskService.deleteTask).not.toHaveBeenCalled();
    });

    it('should navigate to edit task', () => {
      component.edit(mockTasks[0]);

      expect(router.navigate).toHaveBeenCalledWith(['/tasks', 1]);
    });

    it('should navigate to create task', () => {
      component.create();

      expect(router.navigate).toHaveBeenCalledWith(['/tasks/new']);
    });
  });

  describe('pagination', () => {
    it('should go to next page', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));
      component.totalPages = 3;
      component.currentPage = 0;

      component.nextPage();

      expect(component.currentPage).toBe(1);
      expect(taskService.getTasks).toHaveBeenCalled();
    });

    it('should go to previous page', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));
      component.currentPage = 2;

      component.previousPage();

      expect(component.currentPage).toBe(1);
      expect(taskService.getTasks).toHaveBeenCalled();
    });

    it('should not exceed page boundaries', () => {
      component.totalPages = 2;
      component.currentPage = 1;

      component.nextPage();

      expect(component.currentPage).toBe(1);
    });

    it('should change page size', () => {
      taskService.getTasks.and.returnValue(of(mockPagedResponse));

      component.changePageSize(20);

      expect(component.pageSize).toBe(20);
      expect(component.currentPage).toBe(0);
      expect(taskService.getTasks).toHaveBeenCalled();
    });
  });

  describe('utility methods', () => {
    it('should detect overdue tasks', () => {
      const now = new Date();
      const pastDate = new Date(now.getTime() - 86400000); // 1 day ago

      const overdue: Task = {
        id: 1,
        title: 'Overdue',
        dueDate: pastDate.toISOString(),
        isCompleted: false
      };

      expect(component.isOverdue(overdue)).toBe(true);
    });

    it('should not consider completed tasks as overdue', () => {
      const now = new Date();
      const pastDate = new Date(now.getTime() - 86400000);

      const completed: Task = {
        id: 1,
        title: 'Completed',
        dueDate: pastDate.toISOString(),
        isCompleted: true
      };

      expect(component.isOverdue(completed)).toBe(false);
    });

    it('should get correct CSS class for task', () => {
      const completedTask: Task = { id: 1, title: 'Done', isCompleted: true };
      const normalTask: Task = { id: 2, title: 'Normal', isCompleted: false };

      expect(component.getTaskRowClass(completedTask)).toBe('task-row-completed');
      expect(component.getTaskRowClass(normalTask)).toBe('');
    });

    it('should track by task ID', () => {
      const task = mockTasks[0];
      expect(component.trackByTaskId(0, task)).toBe(1);
    });
  });

  describe('ngOnDestroy', () => {
    it('should unsubscribe on destroy', () => {
      spyOn(component['destroy$'], 'next');
      spyOn(component['destroy$'], 'complete');

      component.ngOnDestroy();

      expect(component['destroy$'].next).toHaveBeenCalled();
      expect(component['destroy$'].complete).toHaveBeenCalled();
    });
  });
});

