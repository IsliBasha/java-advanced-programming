# Assignment 06 – Course Enrolment Management API

Spring Boot 3.3.4 / Java 21 REST API for managing course enrolments, backed by MySQL (H2 for tests).

## Architecture
```
controller/ → service/ → repository/ → entity/
                     ↓
               exception/ (GlobalExceptionHandler)
               dto/ (request/response separation)
```

## Setup

### Prerequisites
- Java 21, Maven 3.9+
- MySQL 8+ running locally

### Configure MySQL
Edit `src/main/resources/application.properties`:
```
spring.datasource.password=your_mysql_password
```

Create the database (auto-created by `createDatabaseIfNotExist=true` in the URL).

### Run
```bash
mvn spring-boot:run
```
API available at `http://localhost:8086/api/enrollments`

### Test (no MySQL needed — uses H2)
```bash
mvn test
```

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/enrollments` | Create enrollment |
| GET | `/api/enrollments` | List all (optional `?status=ACTIVE&courseId=CS101&studentId=S001`) |
| GET | `/api/enrollments/{id}` | Get by ID |
| PUT | `/api/enrollments/{id}` | Update enrollment |
| DELETE | `/api/enrollments/{id}` | Delete enrollment |

## Example Request
```bash
curl -X POST http://localhost:8086/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "S001",
    "studentName": "Alice Johnson",
    "courseId": "CS101",
    "courseName": "Java Programming",
    "status": "ACTIVE",
    "completionPercentage": 75.0,
    "enrolledOn": "2024-01-15"
  }'
```

## Validation Rules
- `studentId`, `studentName`, `courseId`, `courseName` — required, non-blank
- `completionPercentage` — 0.0–100.0
- `enrolledOn` — required date
- Duplicate ACTIVE enrolment for same student+course → HTTP 400

## Notes for the Professor
- The duplicate-ACTIVE check lives in the service layer (`EnrollmentService.create()`), not in the controller — controllers stay thin.
- `GlobalExceptionHandler` is annotated with `@RestControllerAdvice` and handles all three error classes (validation, not-found, duplicate) with consistent JSON structure.
- Tests use `@ActiveProfiles("test")` + `application-test.properties` which wires H2 instead of MySQL, so tests run without any database setup.
- `spring.jpa.hibernate.ddl-auto=create-drop` is used in both environments so the schema is always generated fresh from the entity annotations.
