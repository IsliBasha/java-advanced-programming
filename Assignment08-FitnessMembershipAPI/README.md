# Assignment 08 – Fitness Centre Membership API

A Spring Boot REST API for managing fitness-centre members (create, read, update,
delete) with filtering by active status and membership type, **unique email
enforcement**, bean validation, and centralized JSON error handling.

## Tech Stack

- **Java 21**, **Spring Boot 3.3.4**
- Spring Web, Spring Data JPA, Bean Validation (Jakarta)
- **MySQL** at runtime, **H2** (in-memory) for tests
- Maven, JUnit 5

## Architecture

A clean layered design — the controller contains **no business logic**:

```
controller  ->  service  ->  repository  ->  entity
   (HTTP)      (rules,        (Spring Data    (JPA @Entity)
               @Transactional) JPA queries)
```

| Package      | Responsibility                                              |
|--------------|------------------------------------------------------------|
| `entity`     | JPA `Member` entity and `MembershipType` enum              |
| `dto`        | `MemberRequestDto` / `MemberResponseDto` (Java records)    |
| `repository` | `MemberRepository` — Spring Data JPA derived queries       |
| `service`    | `MemberService` — uniqueness rules, lookups, persistence   |
| `controller` | `MemberController` — thin HTTP layer                        |
| `exception`  | Domain exceptions + `GlobalExceptionHandler`               |

## Running Locally

**Prerequisites:** JDK 21, Maven 3.9+, a running MySQL instance.

1. Set your MySQL credentials in `src/main/resources/application.properties`
   (defaults: user `root`, password `your_password`). The database
   `fitness_membership_db` is created automatically.
2. Start the app:
   ```bash
   mvn spring-boot:run
   ```
3. The API listens on **http://localhost:8088**. On startup the schema is
   regenerated (`ddl-auto=create-drop`) and `data.sql` seeds six sample members.

## API Endpoints

Base path: `/api/members`

| Method | Path                 | Description                       | Success |
|--------|----------------------|-----------------------------------|---------|
| POST   | `/api/members`       | Create a member                   | 201     |
| GET    | `/api/members/{id}`  | Get a member by id                | 200     |
| GET    | `/api/members`       | List members (optional filters)   | 200     |
| PUT    | `/api/members/{id}`  | Update a member                   | 200     |
| DELETE | `/api/members/{id}`  | Delete a member                   | 204     |

**Query filters** for `GET /api/members`:

- `active` — `true` or `false`
- `membershipType` — one of `BASIC`, `STANDARD`, `PREMIUM`
- both may be combined: `?active=true&membershipType=PREMIUM`

### curl examples

Create a member:
```bash
curl -X POST http://localhost:8088/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Liam O'\''Connor",
    "email": "liam.oconnor@example.com",
    "membershipType": "PREMIUM",
    "active": true,
    "joinDate": "2025-01-15",
    "monthlyFee": 59.99
  }'
```

List active premium members:
```bash
curl "http://localhost:8088/api/members?active=true&membershipType=PREMIUM"
```

Get / update / delete by id:
```bash
curl http://localhost:8088/api/members/1
curl -X PUT http://localhost:8088/api/members/1 \
  -H "Content-Type: application/json" \
  -d '{ "fullName":"Liam O.","email":"liam.oconnor@example.com","membershipType":"PREMIUM","active":false,"joinDate":"2025-01-15","monthlyFee":59.99 }'
curl -X DELETE http://localhost:8088/api/members/1
```

## Validation Rules

Enforced by Bean Validation on the request DTO (violations return **400** with a
`fieldErrors` map):

| Field            | Rule                                       |
|------------------|--------------------------------------------|
| `fullName`       | not blank                                  |
| `email`          | not blank, valid email format              |
| `membershipType` | required (`BASIC/STANDARD/PREMIUM`)        |
| `active`         | required (`true`/`false`)                  |
| `joinDate`       | required                                   |
| `monthlyFee`     | required, `>= 0.0`                          |

**Business rule (service layer):** email must be unique (case-insensitive). A
collision throws `DuplicateEmailException` and returns **409 Conflict**. The
database also carries a `UNIQUE` constraint on `email` as a final safety net.

## Error Responses

All errors share a consistent JSON shape via `@RestControllerAdvice`:

```json
{
  "timestamp": "2026-06-23T10:00:00",
  "status": 409,
  "error": "Conflict",
  "message": "A member with email 'liam.oconnor@example.com' already exists."
}
```

Validation failures additionally include a `fieldErrors` object mapping each
invalid field to its message.

## Testing

```bash
mvn test
```

Tests run against an in-memory **H2** database (`application-test.properties`,
`@ActiveProfiles("test")`) so **no MySQL is required**. The suite covers create,
duplicate-email rejection (including different casing), single/filtered reads,
update (including the "keep my own email" edge case and cross-member collisions),
and delete (13 tests).

## Notes for the Professor

- Email uniqueness is enforced in two places on purpose: a friendly
  `existsByEmailIgnoreCase` check in the service that returns a clean **409**,
  and a database `UNIQUE` constraint as the ultimate guarantee. The service check
  is for good error messages; the constraint is for correctness under
  concurrency.
- On update I used `existsByEmailIgnoreCaseAndIdNot(...)` so a member can keep
  their own email — only a clash with a *different* member is rejected. This is
  the kind of off-by-one bug that's easy to miss, so it has its own test.
- I returned **409 Conflict** for duplicate emails rather than a generic 400,
  since the request is well-formed — it just conflicts with existing state. The
  two cases stay distinguishable to API clients.
- As with the other APIs, tests run on H2 so `mvn test` works on a clean machine
  with no database setup, while the app targets MySQL at runtime.
