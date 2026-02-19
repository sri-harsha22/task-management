# Quick Reference Guide - Command Cheat Sheet

Handy commands for common tasks. For detailed instructions, see respective documentation files.

---

## 🚀 Quick Start (Full Stack)

### Terminal 1 - Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
# API: http://localhost:8080/api
# Swagger: http://localhost:8080/api/swagger-ui.html
```

### Terminal 2 - Frontend
```bash
cd frontend
npm install
npm start
# App: http://localhost:4200
```

---

## 🏗 Backend Commands

### Build
```bash
cd backend
mvn clean install          # Full build with tests
mvn clean package          # Build without tests
mvn clean package -DskipTests  # Skip tests
```

### Run
```bash
cd backend
mvn spring-boot:run        # Development mode
java -jar target/task-management-api-1.0.0.jar  # Production
```

### Test
```bash
cd backend
mvn test                   # Run all tests
mvn test -Dtest=TaskServiceTest  # Run specific test
mvn test jacoco:report     # With code coverage
```

### Other
```bash
cd backend
mvn dependency:tree        # Check dependencies
mvn org.owasp:dependency-check-maven:check  # Security check
mvn clean                  # Clean build artifacts
```

---

## 🎨 Frontend Commands

### Install & Run
```bash
cd frontend
npm install                # Install dependencies
npm start                  # Run development server (port 4200)
npm start -- --port 4201  # Run on different port
```

### Build
```bash
cd frontend
npm run build              # Production build
npm run build:prod         # Optimized build
```

### Test
```bash
cd frontend
npm test                   # Run unit tests
npm test -- --code-coverage  # With code coverage
npm run e2e               # E2E tests with Playwright
```

### Code Quality
```bash
cd frontend
npm run lint              # Linting
ng format                 # Format code
```

---

## 🐳 Docker Commands

### Build Images
```bash
# Backend
docker build -t task-management-api:1.0.0 backend/

# Frontend
docker build -t task-management-frontend:1.0.0 frontend/
```

### Run Containers
```bash
# Backend with database
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tasks \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  task-management-api:1.0.0

# Frontend
docker run -p 4200:80 task-management-frontend:1.0.0
```

### Docker Compose
```bash
# Full stack
docker-compose up -d       # Start all services
docker-compose down        # Stop all services
docker-compose logs -f     # View logs
docker-compose ps          # View running containers
```

---

## ☸️  Kubernetes Commands

### Create Namespace
```bash
kubectl create namespace task-management
```

### Deploy
```bash
kubectl apply -f backend-deployment.yaml
kubectl apply -f frontend-deployment.yaml
kubectl apply -f services.yaml
```

### View Status
```bash
kubectl get pods -n task-management
kubectl get svc -n task-management
kubectl get deployments -n task-management
```

### Logs & Debug
```bash
kubectl logs -f deployment/task-api -n task-management
kubectl logs -f deployment/task-frontend -n task-management
kubectl describe pod <pod-name> -n task-management
```

### Scale
```bash
kubectl scale deployment task-api --replicas=5 -n task-management
```

---

## 🗄️ Database Commands

### PostgreSQL
```bash
# Connect
psql -h localhost -U postgres -d tasks

# Create database
createdb tasks

# Create user
createuser taskuser -P

# Grant permissions
psql -U postgres -d tasks -c "GRANT ALL PRIVILEGES ON DATABASE tasks TO taskuser"

# Backup
pg_dump -h localhost -U postgres tasks > backup.sql

# Restore
psql -h localhost -U postgres < backup.sql
```

### H2 Console
```
http://localhost:8080/api/h2-console
JDBC URL: jdbc:h2:mem:taskdb
Username: sa
Password: (empty)
```

---

## 🔍 API Testing Commands

### Curl Examples

**Get All Tasks**
```bash
curl http://localhost:8080/api/tasks
```

**Create Task**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Task",
    "description": "Task description",
    "priority": "HIGH",
    "dueDate": "2026-03-15T10:30:00",
    "assignedTo": "John"
  }'
```

**Get Task by ID**
```bash
curl http://localhost:8080/api/tasks/1
```

**Update Task**
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Title", "isCompleted": true}'
```

**Delete Task**
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

**Filter by Status**
```bash
curl "http://localhost:8080/api/tasks/filter/completed?isCompleted=false"
```

**Search**
```bash
curl "http://localhost:8080/api/tasks/search?searchTerm=urgent"
```

**Get Statistics**
```bash
curl http://localhost:8080/api/tasks/statistics
```

---

## 🛠️ Common Development Tasks

### Check Port Usage
```bash
# Find process using port 8080
lsof -i :8080

# Find process using port 4200
lsof -i :4200

# Kill process
kill -9 <PID>
```

### Environment Setup
```bash
# Set Java home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home

# Set Maven home
export M2_HOME=/usr/local/apache-maven-3.9.0
export PATH=$M2_HOME/bin:$PATH

# Check versions
java -version
mvn -version
npm -version
ng version
```

### File Operations
```bash
# List files
ls -la
tree -L 2

# Remove directories
rm -rf node_modules
rm -rf target
rm -rf dist

# Check disk usage
du -sh backend
du -sh frontend
```

---

## 📊 Monitoring Commands

### Health Checks
```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost:4200

# Detailed health
curl http://localhost:8080/actuator/health | jq
```

### Metrics
```bash
# Get metrics list
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Logs
```bash
# View application logs
tail -f logs/application.log

# View with filtering
tail -f logs/application.log | grep ERROR

# Enable debug logging
java -Dlogging.level.com.taskmanagement=DEBUG -jar app.jar
```

---

## 🔧 Troubleshooting Commands

### Restart Services
```bash
# Kill backend process
pkill -f "task-management-api"

# Kill frontend process
pkill -f "npm.*start"

# Clear cache and reinstall
cd frontend && rm -rf node_modules && npm install
cd backend && mvn clean install
```

### Check Configuration
```bash
# Verify Java version (must be 21+)
java -version

# Verify Maven (must be 3.9+)
mvn -version

# Verify Node.js (must be 18+)
node -version

# Verify npm
npm -version
```

### Database Troubleshooting
```bash
# Test database connection
psql -h localhost -U postgres -d tasks -c "SELECT 1"

# Check if PostgreSQL is running
pg_isready -h localhost -p 5432

# View database info
psql -U postgres -l
```

---

## 📦 Package Management

### Backend (Maven)
```bash
cd backend

# Install specific dependency
mvn dependency:tree | grep "artifactId"

# Update dependencies
mvn versions:display-dependency-updates

# Clean dependencies
mvn dependency:purge-local-repository
```

### Frontend (npm)
```bash
cd frontend

# Install dependencies
npm install

# Update dependencies
npm update

# Check outdated packages
npm outdated

# Audit for vulnerabilities
npm audit
```

---

## 🎯 Development Workflow

### Feature Development
```bash
# 1. Create feature branch
git checkout -b feature/my-feature

# 2. Make changes and test
mvn test          # Backend tests
npm test          # Frontend tests

# 3. Build
mvn clean package
npm run build

# 4. Commit changes
git add .
git commit -m "Add my feature"

# 5. Push and create PR
git push origin feature/my-feature
```

### Debugging
```bash
# Backend debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Frontend debug
ng serve --source-map

# Check browser console for errors
# Open DevTools: Ctrl+Shift+I or Cmd+Option+I
```

---

## 📝 Useful Aliases

Add to `.bashrc` or `.zshrc`:

```bash
# Backend
alias backend="cd /path/to/backend"
alias bmvn="backend && mvn"
alias brun="backend && mvn spring-boot:run"
alias btest="backend && mvn test"

# Frontend  
alias frontend="cd /path/to/frontend"
alias fnpm="frontend && npm"
alias fstart="frontend && npm start"
alias ftest="frontend && npm test"
alias fbuild="frontend && npm run build"

# Full stack
alias start-all="(cd backend && mvn spring-boot:run) & (cd frontend && npm start)"
alias test-all="(cd backend && mvn test) & (cd frontend && npm test)"
```

---

## 🔗 Important URLs

| Service | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |
| H2 Console | http://localhost:8080/api/h2-console |
| Actuator Health | http://localhost:8080/actuator/health |

---

## 📞 Getting Help

- **Backend Issues**: See `backend/README.md` or `DEPLOYMENT_UPDATED.md`
- **Frontend Issues**: See `frontend/README_NEW.md`
- **Architecture**: See `ARCHITECTURE.md`
- **Deployment**: See `DEPLOYMENT_UPDATED.md`
- **Full Index**: See `INDEX_UPDATED.md`

---

**Created**: February 20, 2026
**Status**: ✅ Complete Command Reference

