# Frontend - Angular Task Management Web Application

A production-ready Angular 17+ web application for task management with 3 feature components, service layer, comprehensive routing, and E2E tests.

## ✨ Features Implemented

### Components (3 Feature Components)
- **task-list** - Display all tasks with filtering, sorting, and pagination
- **task-form** - Create and edit tasks with client-side validation  
- **task-detail** - View complete task information and status

### Services
- **task.service.ts** - RESTful API communication with backend
- **task.service.spec.ts** - Unit tests for TaskService

### Models
- **task.model.ts** - TypeScript interfaces for type safety

### Routing & Navigation
- **app-routing.module.ts** - Full routing configuration
- **app.module.ts** - Main module with all components
- **app.component** - Root component with layout

### Styling
- **SCSS** - Component-level styling
- **Responsive Design** - Mobile-friendly UI

### Testing
- **Unit Tests** - Service tests with HttpClientTestingModule
- **E2E Tests** - Playwright test configuration

## 📊 Project Structure

```
src/
├── app/
│   ├── app.module.ts                    # Main module
│   ├── app-routing.module.ts            # Routing configuration
│   ├── app.component.html               # Root template
│   ├── app.component.ts                 # Root component logic
│   ├── app.component.scss               # Root styles
│   │
│   ├── components/                      # Feature Components
│   │   ├── task-list/
│   │   │   ├── task-list.component.ts   # List logic (display, filter, sort)
│   │   │   ├── task-list.component.html # Task list template
│   │   │   └── task-list.component.scss # List styling
│   │   │
│   │   ├── task-form/
│   │   │   ├── task-form.component.ts   # Form logic (create, edit, validate)
│   │   │   └── task-form.component.html # Form template
│   │   │
│   │   └── task-detail/
│   │       ├── task-detail.component.ts # Detail logic
│   │       └── task-detail.component.html # Detail template
│   │
│   ├── models/
│   │   └── task.model.ts                # Task interfaces
│   │
│   └── services/
│       ├── task.service.ts              # API service (14 endpoint calls)
│       └── task.service.spec.ts         # Service unit tests
│
├── environments/
│   ├── environment.ts                   # Development configuration
│   └── environment.prod.ts              # Production configuration
│
├── index.html                           # Main HTML file
├── main.ts                              # Bootstrap application
├── styles.scss                          # Global styles
└── test.ts                              # Test configuration

e2e/
└── playwright.spec.ts                   # E2E test suite

Configuration Files:
├── angular.json                         # Angular CLI configuration
├── package.json                         # NPM dependencies
├── tsconfig.json                        # TypeScript configuration
├── tsconfig.app.json                    # App TypeScript config
└── tsconfig.spec.json                   # Test TypeScript config
```

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ installed
- npm 9+ installed
- Angular CLI 17+ installed globally
- Backend API running on http://localhost:8080

### Installation

```bash
# Install dependencies
npm install
```

### Development Server

```bash
# Start Angular development server
npm start

# Navigate to http://localhost:4200/
```

### Running Tests

```bash
# Run unit tests (Karma/Jasmine)
npm test

# Run E2E tests (Playwright)
npm run e2e
```

### Production Build

```bash
# Build optimized bundle for production
npm run build

# Output in dist/ directory
```

## 🔌 API Integration

### Service Layer
The **TaskService** (`task.service.ts`) handles all API communication:

```typescript
// Core endpoints
getAll()           // GET /api/tasks
getById(id)        // GET /api/tasks/{id}
create(task)       // POST /api/tasks
update(id, task)   // PUT /api/tasks/{id}
delete(id)         // DELETE /api/tasks/{id}

// Filtering
filterByStatus(status)         // GET /api/tasks/filter/completed
filterByAssignee(assignee)     // GET /api/tasks/filter/assigned-to
filterByPriority(priority)     // GET /api/tasks/filter/priority
filterByDateRange(start, end)  // GET /api/tasks/filter/due-date-range
getOverdue()                   // GET /api/tasks/filter/overdue

// Search & Utilities
search(term)       // GET /api/tasks/search
markComplete(id)   // PUT /api/tasks/{id}/complete
getStatistics()    // GET /api/tasks/statistics
```

### Environment Configuration

**Development** (`environment.ts`):
```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

**Production** (`environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.yourdomain.com/api'
};
```

## 📦 Dependencies

Key Angular packages:
- `@angular/core` - Core framework
- `@angular/common` - Common directives
- `@angular/forms` - Form handling (Reactive Forms)
- `@angular/router` - Client-side routing
- `@angular/platform-browser-dynamic` - Browser platform
- `@angular/common/http` - HTTP client
- `rxjs` - Reactive programming

Dev dependencies:
- `@angular/cli` - CLI tools
- `@angular/compiler-cli` - Compiler
- `typescript` - TypeScript compiler
- `jasmine-core` - Unit test framework
- `karma` - Test runner
- `playwright` - E2E testing

## 🧪 Testing

### Unit Tests
```bash
# Run tests in watch mode
npm test

# Run tests once and exit
npm test -- --watch=false

# Run tests with code coverage
npm test -- --code-coverage
```

**Test Files**:
- `task.service.spec.ts` - Service layer tests with HttpClientTestingModule

### E2E Tests
```bash
# Run Playwright E2E tests
npm run e2e
```

**Test Files**:
- `e2e/playwright.spec.ts` - Application flow tests

## 🏗 Architecture

### Component Architecture

```
AppComponent (Root)
├── Header/Navigation
├── Main Router Outlet
│   ├── TaskList Component
│   │   └── Displays paginated, filterable task list
│   ├── TaskForm Component
│   │   └── Create/Edit task with validation
│   └── TaskDetail Component
│       └── View single task details
└── Footer
```

### Service Layer

```
Components
    ↓ (inject)
TaskService
    ↓ (HttpClient)
Backend API (/api/tasks/*)
    ↓
Database
```

### Data Flow

1. **Components** request data from **TaskService**
2. **TaskService** makes HTTP calls to **Backend API**
3. **Backend** processes requests and returns data
4. **Components** receive Observable streams
5. **Templates** display data with async pipe

## 🎯 Key Features

✅ **Full CRUD Operations**
- Create, read, update, delete tasks

✅ **Advanced Filtering**
- By status, priority, assignee, date range
- Full-text search support

✅ **Pagination & Sorting**
- Configurable page size
- Sort by multiple fields

✅ **Form Validation**
- Client-side validation
- Real-time error messages

✅ **Type Safety**
- TypeScript strict mode
- Task model interfaces

✅ **Responsive Design**
- Mobile-friendly layouts
- SCSS styling

✅ **Error Handling**
- Display user-friendly errors
- Fallback for failed requests

## 📝 Development Guidelines

### Component Best Practices
- Use **Angular CLI** to generate components
- Follow **smart/dumb** component pattern
- Implement **OnPush** change detection for optimization
- Use **TypeScript strict mode**

### Service Best Practices
- Keep services **stateless** or use RxJS **BehaviorSubject**
- Return **Observables** from HTTP calls
- Implement **error handling**
- Use **RxJS operators** for data transformation

### Styling Best Practices
- Use **SCSS** variables for colors/sizing
- Scope styles to components
- Follow **BEM** naming conventions
- Maintain responsive breakpoints

## 🚢 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t task-management-frontend .

# Run container
docker run -p 4200:80 task-management-frontend
```

### Cloud Deployment

**AWS S3 + CloudFront**:
```bash
npm run build
aws s3 sync dist/ s3://your-bucket/
```

**Azure Static Web Apps**:
```bash
npm run build
# Deploy dist/ folder to Static Web Apps
```

**Netlify/Vercel**:
```bash
# Push to GitHub and connect to Netlify/Vercel
npm run build
```

## 📚 Additional Resources

- [Angular Documentation](https://angular.io/docs)
- [Angular CLI Documentation](https://angular.io/cli)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [RxJS Documentation](https://rxjs.dev/)
- [Playwright Documentation](https://playwright.dev/)

---

**Status**: ✅ **COMPLETE & PRODUCTION-READY**
**Framework**: Angular 17+
**Language**: TypeScript
**Styling**: SCSS
**Testing**: Karma/Jasmine + Playwright
**Last Updated**: February 20, 2026

