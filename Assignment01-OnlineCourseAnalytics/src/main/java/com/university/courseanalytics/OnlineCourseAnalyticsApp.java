package com.university.courseanalytics;

import com.university.courseanalytics.dto.EnrollmentSummaryDto;
import com.university.courseanalytics.model.Enrollment;
import com.university.courseanalytics.service.EnrollmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Entry point for Assignment 01 – Online Course Analytics System.
 * Demonstrates all required features: List, Set, Map, Optional, Stream filtering,
 * Stream mapping, and manual aggregation.
 */
public class OnlineCourseAnalyticsApp {

    public static void main(String[] args) {
        EnrollmentService service = new EnrollmentService();

        // ── Seed realistic data ───────────────────────────────────────────
        service.addEnrollment(new Enrollment("E001", "S001", "Alice Johnson",   "CS101", "Java Programming",    88.5, LocalDate.of(2024, 1, 15)));
        service.addEnrollment(new Enrollment("E002", "S002", "Bob Martinez",    "CS101", "Java Programming",    42.0, LocalDate.of(2024, 1, 20)));
        service.addEnrollment(new Enrollment("E003", "S003", "Clara Schmidt",   "CS202", "Data Structures",     95.0, LocalDate.of(2024, 2, 1)));
        service.addEnrollment(new Enrollment("E004", "S001", "Alice Johnson",   "CS202", "Data Structures",     61.0, LocalDate.of(2024, 2, 5)));
        service.addEnrollment(new Enrollment("E005", "S004", "David Okonkwo",   "CS303", "Algorithms",          78.3, LocalDate.of(2024, 2, 10)));
        service.addEnrollment(new Enrollment("E006", "S005", "Eva Lindström",   "CS303", "Algorithms",          33.7, LocalDate.of(2024, 3, 1)));
        service.addEnrollment(new Enrollment("E007", "S002", "Bob Martinez",    "CS404", "Machine Learning",    55.0, LocalDate.of(2024, 3, 5)));
        service.addEnrollment(new Enrollment("E008", "S006", "Fatima Al-Amin",  "CS404", "Machine Learning",    100.0,LocalDate.of(2024, 3, 8)));

        printHeader("ALL ENROLLMENTS");
        printEnrollments(service.getAllEnrollments());

        // ── Demonstrate Set of unique course IDs ──────────────────────────
        printHeader("UNIQUE COURSE IDs (Set)");
        service.getUniqueCourseIds().forEach(id -> System.out.println("  " + id));

        // ── Demonstrate Map<String, List<Double>> ─────────────────────────
        printHeader("COMPLETION MAP (Map<courseId, List<Double>>)");
        service.getCompletionMap().forEach((courseId, percentages) ->
                System.out.printf("  %-10s → %s%n", courseId, percentages));

        // ── Optional retrieval ────────────────────────────────────────────
        printHeader("Optional LOOKUP: Alice in CS101");
        Optional<Enrollment> found = service.findByStudentAndCourse("S001", "CS101");
        found.ifPresentOrElse(
                e -> System.out.printf("  Found: %s enrolled in %s (%.1f%%)%n",
                        e.studentName(), e.courseName(), e.completionPercentage()),
                () -> System.out.println("  Not found.")
        );

        // ── Stream filter: above threshold ────────────────────────────────
        printHeader("STREAM FILTER: completionPercentage > 70");
        List<Enrollment> highPerformers = service.findAboveThreshold(70.0);
        highPerformers.forEach(e -> System.out.printf(
                "  %-20s %-25s %.1f%%%n", e.studentName(), e.courseName(), e.completionPercentage()));

        // ── Stream filter: below threshold ────────────────────────────────
        printHeader("STREAM FILTER: completionPercentage < 50");
        List<Enrollment> atRisk = service.findBelowThreshold(50.0);
        atRisk.forEach(e -> System.out.printf(
                "  %-20s %-25s %.1f%%%n", e.studentName(), e.courseName(), e.completionPercentage()));

        // ── Stream transformation to DTO ──────────────────────────────────
        printHeader("STREAM MAP → EnrollmentSummaryDto");
        List<EnrollmentSummaryDto> summaries = service.toSummaries();
        summaries.forEach(dto -> System.out.printf(
                "  %-20s | %-25s | %.1f%%%n", dto.studentName(), dto.courseName(), dto.completionPercentage()));

        // ── Manual aggregation (no sum()/average()) ───────────────────────
        printHeader("MANUAL AVERAGE COMPLETION PER COURSE (no stream aggregates)");
        service.getUniqueCourseIds().stream().sorted().forEach(courseId -> {
            double avg = service.computeAverageCompletionForCourse(courseId);
            System.out.printf("  %-10s average: %.2f%%%n", courseId, avg);
        });

        // ── Update & remove ───────────────────────────────────────────────
        printHeader("UPDATE E002: Bob Martinez CS101 → 65.0%");
        Enrollment updated = service.getAllEnrollments().stream()
                .filter(e -> e.enrollmentId().equals("E002"))
                .findFirst()
                .map(e -> e.withCompletionPercentage(65.0))
                .orElseThrow();
        service.updateEnrollment("E002", updated);
        service.findByStudentAndCourse("S002", "CS101")
                .ifPresent(e -> System.out.printf("  Updated: %s → %.1f%%%n", e.studentName(), e.completionPercentage()));

        printHeader("REMOVE E006 (Eva Lindström / Algorithms)");
        boolean removed = service.removeEnrollment("E006");
        System.out.println("  Removed: " + removed);
        System.out.println("  Total enrollments now: " + service.getAllEnrollments().size());
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("═".repeat(60));
        System.out.println("  " + title);
        System.out.println("─".repeat(60));
    }

    private static void printEnrollments(List<Enrollment> list) {
        System.out.printf("  %-6s %-20s %-25s %6s%n", "ID", "Student", "Course", "Done%");
        System.out.println("  " + "─".repeat(56));
        list.forEach(e -> System.out.printf(
                "  %-6s %-20s %-25s %5.1f%%%n",
                e.enrollmentId(), e.studentName(), e.courseName(), e.completionPercentage()));
    }
}
