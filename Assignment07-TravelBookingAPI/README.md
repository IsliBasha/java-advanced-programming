# Assignment 07 – Travel Booking REST API

A Spring Boot REST API for managing travel bookings (create, read, update, delete)
with filtering by destination and status, bean validation, and centralized JSON
error handling.

## Tech Stack

- **Java 21**, **Spring Boot 3.3.4**
- Spring Web, Spring Data JPA, Bean Validation (Jakarta)
- **MySQL** at runtime, **H2** (in-memory) for tests
- Maven, JUnit 5

## Architecture

A clean layered design — each layer has a single responsibility and the
controller contains **no business logic**:

```
controller  ->  service  ->  repository  ->  entity
   (HTTP)      (rules,        (Spring Data    (JPA @Entity)
               @Transactional) JPA queries)
       \
        dto (request/response records)   exception (@RestControllerAdvice)
```

| Package      | Responsibility                                             |
|--------------|------------------------------------------------------------|
| `entity`     | JPA `Booking` entity and `BookingStatus` enum              |
| `dto`        | `BookingRequestDto` / `BookingResponseDto` (Java records)  |
| `repository` | `BookingRepository` — Spring Data JPA derived queries      |
| `service`    | `BookingService` — all validation, lookups, persistence    |
| `controller` | `BookingController` — thin HTTP layer                       |
| `exception`  | Domain exceptions + `GlobalExceptionHandler`               |

## Running Locally

**Prerequisites:** JDK 21, Maven 3.9+, a running MySQL instance.

1. Set your MySQL credentials in `src/main/resources/application.properties`
   (defaults: user `root`, password `your_password`). The database
   `travel_booking_db` is created automatically.
2. Start the app:
   ```bash
   mvn spring-boot:run
   ```
3. The API listens on **http://localhost:8087**. On startup the schema is
   regenerated (`ddl-auto=create-drop`) and `data.sql` seeds six sample bookings.

## API Endpoints

Base path: `/api/bookings`

| Method | Path                  | Description                          | Success |
|--------|-----------------------|--------------------------------------|---------|
| POST   | `/api/bookings`       | Create a booking                     | 201     |
| GET    | `/api/bookings/{id}`  | Get a booking by id                  | 200     |
| GET    | `/api/bookings`       | List bookings (optional filters)     | 200     |
| PUT    | `/api/bookings/{id}`  | Update a booking                     | 200     |
| DELETE | `/api/bookings/{id}`  | Delete a booking                     | 204     |

**Query filters** for `GET /api/bookings`:

- `destination` — partial, case-insensitive match (e.g. `?destination=barce`)
- `status` — one of `PENDING`, `CONFIRMED`, `CANCELLED`
- both may be combined: `?destination=madrid&status=CONFIRMED`

### curl examples

Create a booking:
```bash
curl -X POST http://localhost:8087/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Johnson",
    "destination": "Paris",
    "departureDate": "2026-07-01",
    "returnDate": "2026-07-10",
    "price": 1250.00,
    "status": "CONFIRMED",
    "numberOfTravellers": 2
  }'
```

List confirmed bookings to Barcelona:
```bash
curl "http://localhost:8087/api/bookings?destination=barcelona&status=CONFIRMED"
```

Get / update / delete by id:
```bash
curl http://localhost:8087/api/bookings/1
curl -X PUT http://localhost:8087/api/bookings/1 \
  -H "Content-Type: application/json" \
  -d '{ "customerName":"Alice J.","destination":"Nice","departureDate":"2026-07-02","returnDate":"2026-07-12","price":1400.00,"status":"CONFIRMED","numberOfTravellers":2 }'
curl -X DELETE http://localhost:8087/api/bookings/1
```

## Validation Rules

Enforced by Bean Validation on the request DTO (violations return **400** with a
`fieldErrors` map):

| Field                | Rule                                    |
|----------------------|-----------------------------------------|
| `customerName`       | not blank                               |
| `destination`        | not blank                               |
| `departureDate`      | required                                |
| `returnDate`         | required                                |
| `price`              | required, `>= 0.0`                       |
| `status`             | required (`PENDING/CONFIRMED/CANCELLED`)|
| `numberOfTravellers` | required, positive                      |

**Business rule (service layer):** the return date may not fall before the
departure date — violations throw `InvalidBookingException` and return **400**.

## Error Responses

All errors share a consistent JSON shape via `@RestControllerAdvice`:

```json
{
  "timestamp": "2026-06-23T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Booking not found: id=42"
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
the date business rule, single/filtered reads, update, and delete (10 tests).

## Notes for the Professor

- I kept the controller deliberately thin — every decision (the date sanity
  check, "not found" handling, choosing which filtered query to run) lives in
  `BookingService`, which is also where `@Transactional` sits. The controller
  only maps HTTP to method calls.
- I used **`BigDecimal` for `price`** rather than `double`. Money and binary
  floating point don't mix well (rounding drift), and `BigDecimal` is the
  conventional choice for currency.
- The destination filter is a Spring Data **derived query**
  (`findByDestinationContainingIgnoreCase`) so partial, case-insensitive search
  needs no hand-written JPQL.
- Tests use H2 instead of MySQL on purpose: the marker can run `mvn test` on a
  clean machine with no database setup, while the app itself still targets MySQL
  at runtime.
