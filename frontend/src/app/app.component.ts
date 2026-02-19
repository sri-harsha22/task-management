import { Component } from '@angular/core';

/**
 * AppComponent - Root component of the application
 */
@Component({
  selector: 'app-root',
  template: `
    <div class="app-container">
      <header class="app-header">
        <div class="container">
          <h1>📋 Task Management</h1>
          <nav>
            <a routerLink="/tasks" routerLinkActive="active">Tasks</a>
          </nav>
        </div>
      </header>
      
      <main class="app-main">
        <div class="container">
          <router-outlet></router-outlet>
        </div>
      </main>
      
      <footer class="app-footer">
        <div class="container">
          <p>&copy; 2026 Task Management. All rights reserved.</p>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .app-header {
      background: #007bff;
      color: white;
      padding: 1rem 0;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .app-header h1 {
      margin: 0;
      font-size: 1.5rem;
      color: white;
    }

    .app-header nav {
      margin-top: 0.5rem;
    }

    .app-header nav a {
      color: white;
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: background 0.2s;
    }

    .app-header nav a:hover,
    .app-header nav a.active {
      background: rgba(255, 255, 255, 0.2);
    }

    .app-main {
      flex: 1;
      padding: 2rem 0;
    }

    .app-footer {
      background: #f8f9fa;
      padding: 1rem 0;
      text-align: center;
      border-top: 1px solid #dee2e6;
      color: #6c757d;
    }

    .app-footer p {
      margin: 0;
      font-size: 0.9rem;
    }
  `]
})
export class AppComponent {
  title = 'Task Management';
}

