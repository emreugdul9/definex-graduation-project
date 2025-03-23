# Advanced Task Management Backend

## Overview

This is a Spring Boot-based microservice project that provides task and project management functionalities. It includes user authentication, task assignment, and project tracking.

## Technologies Used

- **Java 21**
- **Spring Boot 3+**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**

## Installation & Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>

2. Navigate to the project directory:
   ```bash
   cd definex-graduation-project
3. Configure database connection in application.properties
   ```bash
   spring.datasource.url=jdbc:postgresql://localhost:5433/dbname
   spring.datasource.username=dbuser
   spring.datasource.password=dbpassword
  4. Build and run rhe application
     ```bash
     mvn clean install
     mvn spring-boot:run

# API ENDPOINTS

## User Controller
`/auth`

### Register
```
POST /auth/register
```
Creates a new user registration.

### Login
```
POST /auth/login
```
Authenticates user and returns JWT token.

## Project Controller 
`/api/project`

### Get Project
```
GET /api/project/{id}
```

### Get All Projects
```
GET /api/project/all
```

### Create Project
```
POST /api/project/create
```

### Update Project
```
PUT /api/project/update/{id}
```

### Delete Project
```
PUT /api/project/delete/{id}
```

### Add Users to Project
```
PUT /api/project/add-user/{projectId}
```

## Task Controller
`/api/task`

### Get Task
```
GET /api/task/{id}
```

### Create Task
```
POST /api/task/create
```

### Check Task Existence
```
GET /api/task/exist/{id}
```

### Change Task State
```
POST /api/task/changeState/{id}
```
State parameter is sent as query.

### Change Task Priority
```
POST /api/task/changePriority/{id}
```
Priority parameter is sent as query.

### Get Task State
```
GET /api/task/state/{id}
```

### Assign Task
```
POST /api/task/assign/{taskId}
```
UserId parameter is sent as query.

## Comment Controller
`/api/comment`

### Add Comment
```
POST /api/comment/addComment/{id}
```

### Get All Comments
```
GET /api/comment/getAll/{id}
```

## Attachment Controller
`/api/attachment`

### Upload Attachment
```
POST /api/attachment/upload/{id}
```
File is sent as form-data.

### Get All Attachments
```
GET /api/attachment/getAll/{id}
```

### Delete Attachment
```
DELETE /api/attachment/delete/{id}
```
