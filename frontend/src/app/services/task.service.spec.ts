import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService, PagedResponse, TaskFilterCriteria } from './task.service';
import { Task } from '../models/task.model';
import { environment } from '../../environments/environment';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/tasks`;

  const mockPagedResponse: PagedResponse<Task> = {
    content: [{ id: 1, title: 'Test', isCompleted: false }],
    totalElements: 1,
    totalPages: 1,
    number: 0,
    size: 10,
    first: true,
    last: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService]
    });
    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should fetch tasks without filters', () => {
    service.getTasks(0, 10).subscribe();
    const req = httpMock.expectOne(request =>
      request.url === baseUrl &&
      request.params.get('page') === '0'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockPagedResponse);
  });

  it('should route to filter/completed when status provided', () => {
    service.getTasks(0, 10, { status: false }).subscribe();
    const req = httpMock.expectOne(request =>
      request.url === `${baseUrl}/filter/completed`
    );
    req.flush(mockPagedResponse);
  });

  it('should route to filter/priority when priority provided', () => {
    service.getTasks(0, 10, { priority: 'HIGH' }).subscribe();
    const req = httpMock.expectOne(request =>
      request.url === `${baseUrl}/filter/priority`
    );
    req.flush(mockPagedResponse);
  });

  it('should route to search when searchTerm provided', () => {
    service.getTasks(0, 10, { searchTerm: 'test' }).subscribe();
    const req = httpMock.expectOne(request =>
      request.url === `${baseUrl}/search`
    );
    req.flush(mockPagedResponse);
  });

  it('should get task by ID', () => {
    service.getTask(1).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('GET');
    req.flush({ id: 1, title: 'Test', isCompleted: false });
  });

  it('should create task', () => {
    service.createTask({ title: 'New' }).subscribe();
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, title: 'New', isCompleted: false });
  });

  it('should update task', () => {
    service.updateTask(1, { title: 'Updated' }).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 1, title: 'Updated', isCompleted: false });
  });

  it('should delete task', () => {
    service.deleteTask(1).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should mark completed', () => {
    service.markCompleted(1).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/1/complete`);
    expect(req.request.method).toBe('PUT');
    req.flush({ id: 1, title: 'Test', isCompleted: true });
  });
});
