# Assignment 01 – Online Course Analytics System

A Maven/Java 21 console application demonstrating advanced Java Collections, `Optional`, and the Stream API.

## Project Structure

```
src/
├── main/java/com/university/courseanalytics/
│   ├── OnlineCourseAnalyticsApp.java   ← entry point / demo
│   ├── model/Enrollment.java           ← immutable record
│   ├── service/EnrollmentService.java  ← business logic
│   └── dto/EnrollmentSummaryDto.java   ← DTO record
└── test/java/com/university/courseanalytics/
    └── EnrollmentServiceTest.java      ← 10 JUnit 5 tests
```

## Data Structures Used

| Structure | Purpose |
|-----------|---------|
| `List<Enrollment>` | Ordered enrolment store with add/remove/update |
| `Set<String>` | Deduplicated set of course IDs |
| `Map<String, List<Double>>` | Per-course completion percentages |

## How to Build and Run

### Prerequisites
- Java 21+
- Maven 3.9+

```bash
# Compile
mvn compile

# Run all tests
mvn test

# Run the demo
mvn package
java -jar target/online-course-analytics-1.0.0.jar
```

## Features Demonstrated

- `List<Enrollment>` with add, remove, and update methods
- `Set<String>` for unique course ID deduplication
- `Map<String, List<Double>>` mapping courseId → list of completion scores
- `Optional<Enrollment>` lookup by `(studentId, courseId)` composite key
- Stream filtering above/below a completion threshold
- Stream transformation to `EnrollmentSummaryDto` objects
- Manual average computation using an ordinary loop (no `sum()`/`average()`)

## Notes for the Professor

- The `Enrollment` type is a Java 21 **record** — constructors are generated automatically and all fields are final, satisfying the immutable-design requirement without boilerplate.
- The manual average in `computeAverageCompletionForCourse` deliberately uses a `for` loop over the `Map`'s value list instead of `Stream.average()`, as the assignment specifies "no aggregate shortcuts".
- `Optional<Enrollment>` lookup is demonstrated at line 52 of `OnlineCourseAnalyticsApp` using `ifPresentOrElse` — run the jar to see both the "found" and (implicitly) the "not found" branch.
- The `withCompletionPercentage` method shows immutable update: it returns a new record rather than mutating the existing one.
