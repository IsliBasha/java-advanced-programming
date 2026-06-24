# Assignment 09 – Digital Media Catalogue API

A Spring Boot REST API for managing a digital media catalogue (movies, music,
e-books, games) with create/read/update/delete, filtering by type and
availability, bean validation (including a **non-negative release year** rule),
and centralized JSON error handling.

## Tech Stack

- **Java 21**, **Spring Boot 3.3.4**
- Spring Web, Spring Data JPA, Bean Validation (Jakarta)
- **MySQL** at runtime, **H2** (in-memory) for tests
- Maven, JUnit 5

## Architecture

A clean layered design — the controller contains **no business logic**:

```
controller  ->  service  ->  repository  ->  entity
   (HTTP)      (filtering,    (Spring Data    (JPA @Entity)
               @Transactional) JPA queries)
```

| Package      | Responsibility                                                |
|--------------|---------------------------------------------------------------|
| `entity`     | JPA `MediaItem` entity and `MediaType` enum                   |
| `dto`        | `MediaItemRequestDto` / `MediaItemResponseDto` (Java records) |
| `repository` | `MediaItemRepository` — Spring Data JPA derived queries       |
| `service`    | `MediaItemService` — filtering, lookups, persistence          |
| `controller` | `MediaItemController` — thin HTTP layer                        |
| `exception`  | `ResourceNotFoundException` + `GlobalExceptionHandler`        |

## Running Locally

**Prerequisites:** JDK 21, Maven 3.9+, a running MySQL instance.

1. Set your MySQL credentials in `src/main/resources/application.properties`
   (defaults: user `root`, password `your_password`). The database
   `digital_media_catalogue_db` is created automatically.
2. Start the app:
   ```bash
   mvn spring-boot:run
   ```
3. The API listens on **http://localhost:8089**. On startup the schema is
   regenerated (`ddl-auto=create-drop`) and `data.sql` seeds seven sample items.

## API Endpoints

Base path: `/api/media`

| Method | Path               | Description                          | Success |
|--------|--------------------|--------------------------------------|---------|
| POST   | `/api/media`       | Create a media item                  | 201     |
| GET    | `/api/media/{id}`  | Get a media item by id               | 200     |
| GET    | `/api/media`       | List items (optional filters)        | 200     |
| PUT    | `/api/media/{id}`  | Update a media item                  | 200     |
| DELETE | `/api/media/{id}`  | Delete a media item                  | 204     |

**Query filters** for `GET /api/media`:

- `type` — one of `MOVIE`, `MUSIC`, `EBOOK`, `GAME`
- `available` — `true` or `false`
- both may be combined: `?type=GAME&available=true`

### curl examples

Create an item:
```bash
curl -X POST http://localhost:8089/api/media \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Inception",
    "type": "MOVIE",
    "genre": "Science Fiction",
    "releaseYear": 2010,
    "available": true
  }'
```

List available games:
```bash
curl "http://localhost:8089/api/media?type=GAME&available=true"
```

Get / update / delete by id:
```bash
curl http://localhost:8089/api/media/1
curl -X PUT http://localhost:8089/api/media/1 \
  -H "Content-Type: application/json" \
  -d '{ "title":"Inception (Remastered)","type":"MOVIE","genre":"Sci-Fi","releaseYear":2010,"available":false }'
curl -X DELETE http://localhost:8089/api/media/1
```

## Validation Rules

Enforced by Bean Validation on the request DTO (violations return **400** with a
`fieldErrors` map):

| Field         | Rule                                  |
|---------------|---------------------------------------|
| `title`       | not blank                             |
| `type`        | required (`MOVIE/MUSIC/EBOOK/GAME`)   |
| `genre`       | not blank                             |
| `releaseYear` | required, **non-negative** (`>= 0`)   |
| `available`   | required (`true`/`false`)             |

## Error Responses

All errors share a consistent JSON shape via `@RestControllerAdvice`:

```json
{
  "timestamp": "2026-06-23T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Media item not found: id=42"
}
```

Validation failures additionally include a `fieldErrors` object mapping each
invalid field to its message, e.g. `{"releaseYear": "Release year cannot be negative"}`.

## Testing

```bash
mvn test
```

Tests run against an in-memory **H2** database (`application-test.properties`,
`@ActiveProfiles("test")`) so **no MySQL is required**. The suite covers CRUD,
all filter combinations, and the bean-validation constraints — including the
non-negative release year rule (13 tests).

## Notes for the Professor

- The non-negative release year is enforced with Jakarta's `@PositiveOrZero` on
  both the entity and the request DTO. I tested it directly with a
  `jakarta.validation.Validator` rather than only through the HTTP layer, so the
  constraint itself is verified in isolation.
- Type and availability filters are Spring Data **derived queries**
  (`findByTypeAndAvailable`, etc.), so no hand-written JPQL is needed. The
  service picks the right query based on which filters are supplied.
- `available` is a primitive `boolean` on the entity (a catalogue item is always
  either available or not) but a nullable `Boolean` query parameter, so "no
  filter" and "filter by false" stay distinguishable.
- As with the other APIs, tests use H2 so `mvn test` works on a clean machine
  with no database setup, while the app targets MySQL at runtime.
