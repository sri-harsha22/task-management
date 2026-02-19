Angular Frontend (sample files)

This folder contains sample Angular `src/app` files (services, components, routing, tests) that can be used inside a real Angular CLI project. The recommended way to get a working frontend:

1. Install Angular CLI (if needed):

```bash
npm install -g @angular/cli
```

2. Create a new Angular project at the workspace root:

```bash
cd frontend
ng new task-manager-frontend --routing --style=scss --strict
cd task-manager-frontend
```

3. Copy the `src/app` files from this `frontend/src/app` folder into your generated project's `src/app` (merge/overwrite as needed).

4. Add `environment` config (example provided in sample files) into `src/environments`.

5. Install dependencies and run:

```bash
npm install
ng serve --open
```

Backend API URL

- The sample service expects the backend to run at `http://localhost:8080` and uses endpoints under `/tasks`.

Provided sample files

- `models/task.model.ts` - Task interfaces
- `services/task.service.ts` - Angular HttpClient service to call the API
- `components/` - `task-list`, `task-detail`, `task-form` components
- `app-routing.module.ts`, `app.module.ts` - wiring for the components
- `services/task.service.spec.ts` - unit test example using HttpClientTestingModule
- `e2e/playwright.spec.ts` - example Playwright test (requires Playwright setup)

Notes

- These files are intended to be copied into an Angular CLI project. They are small, focused examples showing routing, state management via a service, form validation, and basic client-side filtering/pagination hooks.
- If you want, I can generate a complete Angular project here (it will be large). Tell me if you want me to create the full `task-manager-frontend` app in `frontend/` automatically.
