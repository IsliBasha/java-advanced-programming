package com.university.courseanalytics.service;

import com.university.courseanalytics.dto.EnrollmentSummaryDto;
import com.university.courseanalytics.model.Enrollment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages enrollments using a List, a Set of unique course IDs,
 * and a Map of courseId -> List of completion percentages.
 */
public class EnrollmentService {

    private final List<Enrollment> enrollments = new ArrayList<>();
    private final Set<String> uniqueCourseIds = new HashSet<>();
    private final Map<String, List<Double>> completionMap = new HashMap<>();

    // ── CRUD ─────────────────────────────────────────────────────────────

    /** Adds an enrollment, updating the course-ID set and completion map. */
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        uniqueCourseIds.add(enrollment.courseId());
        completionMap
                .computeIfAbsent(enrollment.courseId(), k -> new ArrayList<>())
                .add(enrollment.completionPercentage());
    }

    /** Removes all enrollments for the given enrollmentId. */
    public boolean removeEnrollment(String enrollmentId) {
        boolean removed = enrollments.removeIf(e -> e.enrollmentId().equals(enrollmentId));
        rebuildDerivedCollections();
        return removed;
    }

    /**
     * Replaces an existing enrollment (matched by enrollmentId) with an updated one.
     * Returns true if replacement succeeded.
     */
    public boolean updateEnrollment(String enrollmentId, Enrollment updated) {
        for (int i = 0; i < enrollments.size(); i++) {
            if (enrollments.get(i).enrollmentId().equals(enrollmentId)) {
                enrollments.set(i, updated);
                rebuildDerivedCollections();
                return true;
            }
        }
        return false;
    }

    // ── QUERY ─────────────────────────────────────────────────────────────

    /** Retrieves an enrollment by composite key (studentId + courseId). */
    public Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId) {
        return enrollments.stream()
                .filter(e -> e.studentId().equals(studentId) && e.courseId().equals(courseId))
                .findFirst();
    }

    /** Returns all enrollments with completion percentage strictly above the threshold. */
    public List<Enrollment> findAboveThreshold(double threshold) {
        return enrollments.stream()
                .filter(e -> e.completionPercentage() > threshold)
                .collect(Collectors.toList());
    }

    /** Returns all enrollments with completion percentage strictly below the threshold. */
    public List<Enrollment> findBelowThreshold(double threshold) {
        return enrollments.stream()
                .filter(e -> e.completionPercentage() < threshold)
                .collect(Collectors.toList());
    }

    /** Maps enrollments to lightweight DTO objects via a Stream transformation. */
    public List<EnrollmentSummaryDto> toSummaries() {
        return enrollments.stream()
                .map(e -> new EnrollmentSummaryDto(e.studentName(), e.courseName(), e.completionPercentage()))
                .collect(Collectors.toList());
    }

    /**
     * Computes the average completion for a course WITHOUT using stream aggregate methods.
     * Iterates the completionMap list manually to satisfy the assignment constraint.
     */
    public double computeAverageCompletionForCourse(String courseId) {
        List<Double> percentages = completionMap.getOrDefault(courseId, Collections.emptyList());
        if (percentages.isEmpty()) return 0.0;

        double total = 0.0;
        for (double p : percentages) {
            total += p;
        }
        return total / percentages.size();
    }

    // ── ACCESSORS ─────────────────────────────────────────────────────────

    public List<Enrollment> getAllEnrollments() {
        return Collections.unmodifiableList(enrollments);
    }

    public Set<String> getUniqueCourseIds() {
        return Collections.unmodifiableSet(uniqueCourseIds);
    }

    public Map<String, List<Double>> getCompletionMap() {
        return Collections.unmodifiableMap(completionMap);
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────

    private void rebuildDerivedCollections() {
        uniqueCourseIds.clear();
        completionMap.clear();
        for (Enrollment e : enrollments) {
            uniqueCourseIds.add(e.courseId());
            completionMap.computeIfAbsent(e.courseId(), k -> new ArrayList<>()).add(e.completionPercentage());
        }
    }
}
