# Assignment 02 – Travel Booking Management System

A Maven/Java 21 console application demonstrating advanced Java Collections, `Optional`, and the Stream API for travel booking management.

## Project Structure

```
src/
├── main/java/com/university/travelbooking/
│   ├── TravelBookingApp.java           ← entry point / demo
│   ├── model/Booking.java              ← immutable record
│   ├── model/BookingStatus.java        ← enum (PENDING/CONFIRMED/CANCELLED)
│   ├── service/BookingService.java     ← business logic
│   └── dto/BookingDto.java             ← lightweight DTO record
└── test/java/com/university/travelbooking/
    └── BookingServiceTest.java         ← 9 JUnit 5 tests
```

## How to Build and Run

```bash
mvn compile       # compile only
mvn test          # run all tests
mvn package
java -jar target/travel-booking-management-1.0.0.jar
```

## Features Demonstrated

| Requirement | Implementation |
|-------------|----------------|
| `List<Booking>` | `ArrayList` with add/remove/update |
| `Set<String>` destinations | `HashSet` of unique destination strings |
| `Map<String, Set<String>>` | customerId → set of destinations visited |
| `Optional<Booking>` | `findById()` with `ifPresentOrElse` |
| Stream filter by departure range | `findByDepartureRange(from, to)` |
| Stream map to DTO | `toBookingDtos()` → `List<BookingDto>` |
| Partial destination search | case-insensitive `contains` in stream filter |
| Price range filtering | `findByPriceRange(min, max)` |
| Loop-based total | `computeTotal()` uses `for` loop, no `sum()` |

## Notes for the Professor

- `BookingDto` is a record with exactly `(customerName, destination)` — the stream map requirement is demonstrated in `toBookingDtos()` in the app class.
- The `Map<String, Set<String>>` value type uses `Set` (not `List`) deliberately to prevent the same destination appearing twice for the same customer if they make two bookings to the same place.
- The `computeTotal` method uses an ordinary `for-each` loop as required — it's a separate method so its intent is obvious to the grader.
- Run the jar to see departure-range and price-range filtering, partial search, and manual total computation all printed with column-aligned console output.
