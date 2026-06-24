package com.university.courseanalytics;

import com.university.courseanalytics.model.Enrollment;
import com.university.courseanalytics.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EnrollmentService Tests")
class EnrollmentServiceTest {

    private EnrollmentService service;
    private static final LocalDate DATE = LocalDate.of(2024, 1, 1);

    @BeforeEach
    void setUp() {
        service = new EnrollmentService();
        service.addEnrollment(new Enrollment("E001", "S001", "Alice", "CS101", "Java", 90.0, DATE));
        service.addEnrollment(new Enrollment("E002", "S002", "Bob",   "CS101", "Java", 40.0, DATE));
        service.addEnrollment(new Enrollment("E003", "S001", "Alice", "CS202", "Data", 60.0, DATE));
    }

    @Test
    @DisplayName("addEnrollment populates list, set, and map")
    void testAddEnrollmentPopulatesCollections() {
        assertEquals(3, service.getAllEnrollments().size());
        assertEquals(2, service.getUniqueCourseIds().size());
        assertTrue(service.getCompletionMap().containsKey("CS101"));
        assertEquals(2, service.getCompletionMap().get("CS101").size());
    }

    @Test
    @DisplayName("findByStudentAndCourse returns present Optional for known pair")
    void testFindByStudentAndCourse_found() {
        Optional<Enrollment> result = service.findByStudentAndCourse("S001", "CS101");
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().studentName());
    }

    @Test
    @DisplayName("findByStudentAndCourse returns empty Optional for unknown pair")
    void testFindByStudentAndCourse_notFound() {
        Optional<Enrollment> result = service.findByStudentAndCourse("S999", "CS101");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAboveThreshold returns only enrollments above given threshold")
    void testFindAboveThreshold() {
        List<Enrollment> result = service.findAboveThreshold(70.0);
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).studentName());
        assertEquals("CS101", result.get(0).courseId());
    }

    @Test
    @DisplayName("findBelowThreshold returns only enrollments below given threshold")
    void testFindBelowThreshold() {
        List<Enrollment> result = service.findBelowThreshold(50.0);
        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).studentName());
    }

    @Test
    @DisplayName("toSummaries maps to DTO without losing data")
    void testToSummaries() {
        var summaries = service.toSummaries();
        assertEquals(3, summaries.size());
        assertTrue(summaries.stream().anyMatch(d -> d.studentName().equals("Alice") && d.completionPercentage() == 90.0));
    }

    @Test
    @DisplayName("computeAverageCompletionForCourse computes correctly without stream aggregates")
    void testManualAverage() {
        double avg = service.computeAverageCompletionForCourse("CS101");
        assertEquals(65.0, avg, 0.001);
    }

    @Test
    @DisplayName("removeEnrollment removes entry and updates derived collections")
    void testRemoveEnrollment() {
        service.removeEnrollment("E002");
        assertEquals(2, service.getAllEnrollments().size());
        assertEquals(1, service.getCompletionMap().get("CS101").size());
    }

    @Test
    @DisplayName("updateEnrollment replaces enrollment in place")
    void testUpdateEnrollment() {
        Enrollment replacement = new Enrollment("E001", "S001", "Alice", "CS101", "Java", 99.0, DATE);
        boolean result = service.updateEnrollment("E001", replacement);
        assertTrue(result);
        assertEquals(99.0, service.findByStudentAndCourse("S001", "CS101").orElseThrow().completionPercentage());
    }

    @Test
    @DisplayName("Enrollment record rejects invalid completion percentage")
    void testInvalidCompletionPercentage() {
        assertThrows(IllegalArgumentException.class,
                () -> new Enrollment("E999", "S001", "Test", "CS101", "Java", 150.0, DATE));
    }
}
