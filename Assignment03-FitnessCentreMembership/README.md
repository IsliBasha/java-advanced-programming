# Assignment 03 – Fitness Centre Membership System

Maven/Java 21 console app demonstrating Collections, `Optional`, and Stream API for fitness centre membership management.

## Project Structure
```
src/main/java/com/university/fitnesscentre/
├── FitnessCentreMembershipApp.java    ← entry point / demo
├── model/Member.java                  ← immutable record
├── model/MembershipType.java          ← enum (BASIC/STANDARD/PREMIUM)
├── service/MembershipService.java     ← business logic
└── dto/MemberSummaryDto.java          ← DTO record
```

## How to Run
```bash
mvn test
mvn package && java -jar target/fitness-centre-membership-1.0.0.jar
```

## Notes for the Professor
- `Member.attendedClasses` is stored as `List.copyOf(...)` inside the record compact constructor — any external list passed in is defensively copied so the record stays truly immutable.
- `mapNamesToAttendedClassCounts()` uses `Collectors.toMap` with a merge function to handle the edge case of two members sharing the same name (counts are summed).
- Combined active + type filtering is a single `.filter()` call with `&&` rather than chained streams — cleaner and avoids two passes over the data.
- The `Map<String, List<Member>>` is keyed by class-type string and rebuilt via `rebuildDerivedCollections()` whenever a member is removed or updated, keeping the map consistent.
