# Assignment 05 – Digital Media Collection Manager

Maven/Java 21 console application demonstrating Collections, `Optional`, and Stream API for a digital media library.

## Project Structure
```
src/main/java/com/university/digitalmedia/
├── DigitalMediaCollectionApp.java     ← entry point / demo
├── model/MediaItem.java               ← immutable record (durationMinutes nullable for ebooks)
├── model/MediaType.java               ← enum (MOVIE/MUSIC/EBOOK/GAME)
├── service/MediaCollectionService.java← business logic
└── dto/MediaSummaryDto.java           ← DTO record
```

## How to Run
```bash
mvn test
mvn package && java -jar target/digital-media-collection-1.0.0.jar
```

## Notes for the Professor
- `durationMinutes` is `Integer` (nullable) rather than `int` — this correctly models the business rule that e-books don't have a duration without using a sentinel value like `-1`.
- The combined filter `findByTypeGenreAndMinDuration` chains three conditions in one `.filter()` with null guard on `durationMinutes` before comparing — no NPE risk.
- The two `Optional` lookups (`findById` vs `findByExactTitle`) demonstrate both ID-based and title-based exact search, per the assignment requirement.
- `mapTitlesToDurations()` excludes null-duration items via an upstream `.filter(i -> i.durationMinutes() != null)` before `Collectors.toMap`, keeping the map clean.
