# Full-Stack Deployment & Operations Guide

## Overview

Comprehensive guidance for deploying and operating both the **Spring Boot Backend** and **Angular Frontend** of the Task Management application across development, staging, and production environments.

---

## Quick Start

### Development (Local)

**Terminal 1 - Backend**:
```bash
cd backend
mvn clean install
mvn spring-boot:run
# API: http://localhost:8080/api
# Swagger: http://localhost:8080/api/swagger-ui.html
```

**Terminal 2 - Frontend**:
```bash
cd frontend
npm install
npm start
# App: http://localhost:4200
```

---

## Backend Deployment

### Prerequisites
- Java 21 or higher
- Maven 3.9 or higher
- PostgreSQL 15+ (production)

### Build

```bash
cd backend
mvn clean package
# Output: target/task-management-api-1.0.0.jar (~50MB)
```

### Run

```bash
# Development
java -jar target/task-management-api-1.0.0.jar

# Production
java -Dspring.profiles.active=prod \
     -Xms1G -Xmx2G \
     -jar target/task-management-api-1.0.0.jar
```

### Configuration

**Environment Variables**:
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/tasks
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=secret

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

# Server
SERVER_PORT=8080

# Logging
LOGGING_LEVEL_COM_TASKMANAGEMENT=INFO
```

### Testing

```bash
cd backend
mvn test
# Results: 16 tests passing (100%)
```

---

## Frontend Deployment

### Prerequisites
- Node.js 18+
- npm 9+

### Development Build

```bash
cd frontend
npm install
npm start
# App: http://localhost:4200
```

### Production Build

```bash
cd frontend
npm install
npm run build
# Output: dist/ (optimized bundle)
```

### Configuration

**Environment Setup**:

**Development** (environment.ts):
```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080/api'
};
```

**Production** (environment.prod.ts):
```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.yourdomain.com/api'
};
```

### Testing

```bash
cd frontend
npm test          # Unit tests (Karma/Jasmine)
npm run e2e       # E2E tests (Playwright)
```

---

## Docker Deployment

### Backend Docker Image

**Dockerfile** (backend/Dockerfile):
```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/task-management-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

**Build & Run**:
```bash
# Build
docker build -t task-management-api:1.0.0 backend/

# Run with database
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tasks \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  task-management-api:1.0.0
```

### Frontend Docker Image

**Dockerfile** (frontend/Dockerfile):
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/* /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Build & Run**:
```bash
# Build
docker build -t task-management-frontend:1.0.0 frontend/

# Run
docker run -p 4200:80 task-management-frontend:1.0.0
```

### Docker Compose (Full Stack)

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: tasks
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/tasks
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/tasks/statistics"]
      interval: 30s
      timeout: 10s
      retries: 3

  frontend:
    build:
      context: frontend
    ports:
      - "4200:80"
    depends_on:
      - backend
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
```

**Run Full Stack**:
```bash
docker-compose up -d
```

---

## Kubernetes Deployment

### Prerequisites
- kubectl configured
- Docker images pushed to registry
- Namespace created

### Create Namespace

```bash
kubectl create namespace task-management
```

### PostgreSQL Secret

```bash
kubectl create secret generic db-secret \
  --from-literal=url=jdbc:postgresql://postgres:5432/tasks \
  --from-literal=username=postgres \
  --from-literal=password=secret \
  -n task-management
```

### Backend Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-api
  namespace: task-management
spec:
  replicas: 3
  selector:
    matchLabels:
      app: task-api
  template:
    metadata:
      labels:
        app: task-api
    spec:
      containers:
      - name: task-api
        image: registry.example.com/task-management-api:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /api/tasks/statistics
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/tasks/statistics
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

### Frontend Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-frontend
  namespace: task-management
spec:
  replicas: 2
  selector:
    matchLabels:
      app: task-frontend
  template:
    metadata:
      labels:
        app: task-frontend
    spec:
      containers:
      - name: task-frontend
        image: registry.example.com/task-management-frontend:1.0.0
        ports:
        - containerPort: 80
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 5
          periodSeconds: 5
```

### Services

```yaml
apiVersion: v1
kind: Service
metadata:
  name: task-api-service
  namespace: task-management
spec:
  type: LoadBalancer
  ports:
  - port: 8080
    targetPort: 8080
  selector:
    app: task-api

---
apiVersion: v1
kind: Service
metadata:
  name: task-frontend-service
  namespace: task-management
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 80
  selector:
    app: task-frontend
```

### Deploy

```bash
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml
kubectl apply -f services.yaml

# Check status
kubectl get pods -n task-management
kubectl get svc -n task-management

# View logs
kubectl logs -f deployment/task-api -n task-management
```

---

## Database Setup

### PostgreSQL Setup

```bash
# Create database
createdb tasks

# Create user
createuser -U postgres taskuser

# Grant permissions
psql -U postgres -d tasks << EOF
GRANT ALL PRIVILEGES ON DATABASE tasks TO taskuser;
CREATE SCHEMA IF NOT EXISTS public;
GRANT ALL ON SCHEMA public TO taskuser;
EOF
```

### Schema Creation

**First Run** (Auto-create):
```bash
export SPRING_JPA_HIBERNATE_DDL_AUTO=create
mvn spring-boot:run
```

**Production** (Validate only):
```bash
export SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

### Performance Indexes

```sql
-- Connect to tasks database
CREATE INDEX idx_task_completion ON tasks(is_completed);
CREATE INDEX idx_task_due_date ON tasks(due_date);
CREATE INDEX idx_task_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_task_created_at ON tasks(created_at);
CREATE INDEX idx_task_priority ON tasks(priority);

-- Full-text search (optional)
CREATE INDEX idx_task_search ON tasks USING GIN(
  to_tsvector('english', title || ' ' || COALESCE(description, ''))
);
```

---

## Monitoring & Logging

### Backend Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health
curl http://localhost:8080/actuator/health | jq

# Metrics
curl http://localhost:8080/actuator/metrics
```

### Application Logs

```bash
# View logs
tail -f logs/application.log

# With filtering
tail -f logs/application.log | grep ERROR

# Enable debug logging
java -Dlogging.level.com.taskmanagement=DEBUG -jar app.jar
```

### Frontend Logs

```bash
# Browser console errors
# Check browser Developer Tools > Console tab

# Server logs (if using Nginx)
docker logs <container-id>
```

---

## Backup & Recovery

### Database Backup

```bash
# Full backup
pg_dump -h localhost -U postgres tasks > backup-$(date +%Y%m%d-%H%M%S).sql

# Compressed backup
pg_dump -h localhost -U postgres tasks | gzip > backup-$(date +%Y%m%d-%H%M%S).sql.gz

# Restore
psql -h localhost -U postgres < backup-2026-02-20-100000.sql

# Restore from compressed
gunzip -c backup-2026-02-20-100000.sql.gz | psql -h localhost -U postgres
```

### Automated Backups

```bash
#!/bin/bash
# backup.sh
BACKUP_DIR="/backups"
DB_NAME="tasks"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR
pg_dump -h localhost -U postgres -F c $DB_NAME > $BACKUP_DIR/backup_$TIMESTAMP.dump
gzip $BACKUP_DIR/backup_$TIMESTAMP.dump

# Delete backups older than 30 days
find $BACKUP_DIR -name "*.dump.gz" -mtime +30 -delete
```

**Cron Schedule**:
```bash
# Daily backup at 2 AM
0 2 * * * /path/to/backup.sh
```

---

## Troubleshooting

### Backend Issues

**Port Already in Use**:
```bash
lsof -i :8080
kill -9 <PID>
```

**Memory Errors**:
```bash
# Increase heap
java -Xms1G -Xmx2G -jar app.jar
```

**Database Connection Failed**:
```bash
# Test connection
psql -h localhost -U postgres -d tasks -c "SELECT 1"

# Check if PostgreSQL is running
pg_isready -h localhost -p 5432
```

### Frontend Issues

**Port Already in Use**:
```bash
ng serve --port 4201
```

**Dependencies Issues**:
```bash
rm -rf node_modules package-lock.json
npm install
```

**Build Issues**:
```bash
npm run build --prod
```

---

## Production Checklist

- [ ] Java 21 installed and verified
- [ ] Node.js 18+ installed and verified
- [ ] PostgreSQL database created and indexed
- [ ] Automated backups configured
- [ ] SSL/TLS certificates installed
- [ ] Environment variables configured
- [ ] Health checks configured and tested
- [ ] Monitoring and alerting enabled
- [ ] Logging aggregation set up
- [ ] Rate limiting configured
- [ ] Security audit completed
- [ ] Performance tested under load
- [ ] Disaster recovery plan documented

---

**Created**: February 20, 2026
**Status**: ✅ Complete Full-Stack Deployment Guide

