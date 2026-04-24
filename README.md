# Task Management System

A RESTful Task Management API built with Spring Boot, featuring JWT authentication,
task assignment, status tracking, and a commenting system.

## Technologies

- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- H2 Database
- BCrypt Password Encoding

## Features

- User registration and authentication
- JWT token-based authorization
- Create and manage tasks
- Assign tasks to registered users
- Update task status
- Comment on tasks
- Filter tasks by author and/or assignee

## API Endpoints

### Authentication
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/accounts` | Register a new user | Public |
| POST | `/api/auth/token` | Get JWT token | Basic Auth |

### Tasks
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/tasks` | Create a new task | Authenticated |
| GET | `/api/tasks` | Get all tasks (filter by author/assignee) | Authenticated |
| PUT | `/api/tasks/{taskId}/assign` | Assign a task to a user | Task Author |
| PUT | `/api/tasks/{taskId}/status` | Update task status | Author or Assignee |

### Comments
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/tasks/{taskId}/comments` | Add a comment to a task | Authenticated |
| GET | `/api/tasks/{taskId}/comments` | Get all comments for a task | Authenticated |

## Getting Started

### Prerequisites
- Java 17+
- Gradle

### Running the Application
```bash
git clone https://github.com/mohaibra1/Task-Management-System.git
cd task-management
./gradlew bootRun
```

The app runs on `http://localhost:8080`

## Usage Example

### 1. Register a user
```http
POST /api/accounts
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "secret123"
}
```

### 2. Get a JWT token
```http
POST /api/auth/token
Authorization: Basic dXNlckBleGFtcGxlLmNvbTpzZWNyZXQxMjM=
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Create a task
```http
POST /api/tasks
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "title": "My first task",
  "description": "This is a task description"
}
```

Response:
```json
{
  "id": "1",
  "title": "My first task",
  "description": "This is a task description",
  "status": "CREATED",
  "author": "user@example.com",
  "assignee": "none"
}
```

### 4. Assign a task
```http
PUT /api/tasks/1/assign
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "assignee": "other@example.com"
}
```

### 5. Update task status
```http
PUT /api/tasks/1/status
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

## Task Status Flow
## Security

- Passwords are hashed using BCrypt
- Stateless authentication using JWT tokens
- Tokens expire after 1 hour
- Basic Auth is only used for the `/api/auth/token` endpoint
