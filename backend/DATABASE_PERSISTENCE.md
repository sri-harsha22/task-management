# Database Persistence Configuration

## Overview
The Task Management application now uses **file-based H2 database** for persistent data storage. All task data will survive server restarts.

## Configuration Changes

### Database URL
- **Before**: `jdbc:h2:mem:taskdb` (in-memory, data lost on restart)
- **After**: `jdbc:h2:file:./data/taskdb` (file-based, data persisted to disk)

### Hibernate DDL Mode
- **Before**: `create-drop` (drops database on shutdown)
- **After**: `update` (preserves data, updates schema automatically)

## Database File Location
The database files are stored in:
```
backend/data/taskdb.mv.db
backend/data/taskdb.trace.db (optional, for tracing)
```

## Features
- **Automatic Persistence**: All data is automatically saved to disk
- **Schema Updates**: Database schema updates automatically when you modify entities
- **Data Integrity**: Uses `DB_CLOSE_ON_EXIT=FALSE` and `AUTO_RECONNECT=TRUE` for reliability
- **H2 Console**: Still available at http://localhost:9090/api/h2-console for database inspection

## H2 Console Access
To view/manage your data via H2 Console:
1. Navigate to: http://localhost:9090/api/h2-console
2. Use these connection settings:
   - **JDBC URL**: `jdbc:h2:file:./data/taskdb`
   - **Username**: `sa`
   - **Password**: (leave empty)
   - **Driver Class**: `org.h2.Driver`

## Data Management

### Backup Database
To backup your data:
```bash
cp -r backend/data backend/data-backup-$(date +%Y%m%d)
```

### Reset Database
To start fresh (deletes all data):
```bash
rm -rf backend/data/
```

### Migration to Production Database
For production use, consider migrating to:
- **PostgreSQL** (recommended)
- **MySQL/MariaDB**
- **Oracle**

The JPA entities are database-agnostic, making migration straightforward.

## Important Notes
- The `data/` directory is added to `.gitignore` to avoid committing database files
- First restart after this change will preserve any existing data
- Database files are created automatically if they don't exist
- For production deployment, use a proper database server (PostgreSQL, MySQL, etc.)

