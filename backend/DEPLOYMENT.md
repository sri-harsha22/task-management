# Deployment & Operations Guide

## Overview

This document provides comprehensive guidance for deploying and operating the Task Management API in different environments.

## Quick Start

### Local Development

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Application will start on http://localhost:8080
```

### Docker

```bash
# Build image
docker build -t task-management-api:1.0.0 .

# Run container
docker run -p 8080:8080 task-management-api:1.0.0
```

### Docker Compose

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down
```

### Kubernetes

```bash
# Deploy to Kubernetes
kubectl apply -f deployment.yaml

# Check status
kubectl get pods -n task-management

# View logs
kubectl logs -f deployment/task-management-api -n task-management
```

---

## Development Setup

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher
- (Optional) Docker for containerization
- (Optional) Kubernetes for orchestration

### Building

```bash
# Build with tests
mvn clean package

# Build without tests
mvn clean package -DskipTests

# Run tests
mvn test
```

---

## Database

### H2 (Development)

- **Type**: In-memory
- **Access**: http://localhost:8080/api/h2-console
- **JDBC URL**: jdbc:h2:mem:taskdb
- **Username**: sa
- **Password**: (leave blank)

### PostgreSQL (Production)

Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tasks
    username: postgres
    password: secret
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
```

### Database Migration

```bash
# Create indexes for production
CREATE INDEX idx_task_completion ON tasks(is_completed);
CREATE INDEX idx_task_due_date ON tasks(due_date);
CREATE INDEX idx_task_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_task_created_at ON tasks(created_at);
```

---

## Configuration

### Application Properties

File: `src/main/resources/application.yml`

```yaml
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Logging
logging.level.root=INFO
logging.level.com.taskmanagement=DEBUG

# Swagger
springdoc.swagger-ui.enabled=true
```

### Spring Profiles

```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Test
mvn test
```

---

## Monitoring & Logging

### Application Logs

```bash
# View logs
tail -f logs/application.log

# With grep filter
tail -f logs/application.log | grep ERROR

# Docker logs
docker logs -f <container-id>

# Kubernetes logs
kubectl logs -f deployment/task-management-api -n task-management
```

### Health Check

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check database
curl http://localhost:8080/actuator/health | jq '.components.db'
```

---

## Performance Tuning

### JVM Configuration

```bash
# Increase heap size
java -Xms1G -Xmx2G -jar target/task-management-api-1.0.0.jar

# With garbage collection optimization
java -Xms1G -Xmx2G -XX:+UseG1GC -jar target/task-management-api-1.0.0.jar
```

### Database Connection Pooling

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### Batch Processing

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

---

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>

# Use different port
java -Dserver.port=9090 -jar target/task-management-api-1.0.0.jar
```

### OutOfMemoryError

```bash
# Increase heap size
export JAVA_OPTS="-Xmx2G"
mvn spring-boot:run
```

### Database Connection Timeout

```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 60000
      maximum-pool-size: 30
```

### Slow Queries

```sql
-- Check query performance
EXPLAIN ANALYZE SELECT * FROM tasks WHERE is_completed = false;

-- Create missing indexes
CREATE INDEX idx_task_completion ON tasks(is_completed);
```

---

## Backup & Recovery

### PostgreSQL Backup

```bash
# Backup database
pg_dump -h localhost -U postgres tasks > backup-$(date +%Y%m%d-%H%M%S).sql

# Backup with compression
pg_dump -h localhost -U postgres -F c tasks > backup.dump

# Restore database
psql -h localhost -U postgres < backup.sql

# Restore from dump
pg_restore -h localhost -U postgres -d tasks backup.dump
```

### Automated Backup

```bash
#!/bin/bash
# backup.sh
BACKUP_DIR="/backups"
DB_NAME="tasks"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR
pg_dump -h localhost -U postgres -F c $DB_NAME > $BACKUP_DIR/backup_$TIMESTAMP.dump
gzip $BACKUP_DIR/backup_$TIMESTAMP.dump
find $BACKUP_DIR -name "*.dump.gz" -mtime +30 -delete
```

Add to crontab:
```bash
0 2 * * * /path/to/backup.sh
```

---

## Production Checklist

- [ ] Java 21 installed and configured
- [ ] PostgreSQL database set up
- [ ] Database backups configured
- [ ] HTTPS/TLS certificates installed
- [ ] Authentication (JWT/OAuth2) implemented
- [ ] Rate limiting configured
- [ ] Monitoring and alerting set up
- [ ] Log aggregation configured
- [ ] Health checks configured
- [ ] Auto-scaling configured
- [ ] Disaster recovery plan tested
- [ ] Security audit completed

---

## Useful Commands

```bash
# Build and run
mvn clean package && java -jar target/task-management-api-1.0.0.jar

# Run with profile
java -Dspring.profiles.active=prod -jar target/task-management-api-1.0.0.jar

# Check dependencies
mvn dependency:tree

# Check for vulnerabilities
mvn org.owasp:dependency-check-maven:check

# Run tests
mvn clean test

# Generate test coverage
mvn clean test jacoco:report
```

---

## Docker Files

### Dockerfile

```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/task-management-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: tasks
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  task-api:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/tasks
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

---

## Kubernetes Files

See separate deployment manifests for Kubernetes configuration:
- `deployment.yaml` - Application deployment
- `service.yaml` - Service configuration
- `hpa.yaml` - Horizontal Pod Autoscaler (optional)

---

## Complete Documentation

For full deployment, monitoring, and operations details, see the comprehensive deployment guide in the root project directory.

---

**Created**: February 20, 2026
**Version**: 1.0.0

