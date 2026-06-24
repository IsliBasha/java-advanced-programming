package com.university.courseanalytics.model;

import java.time.LocalDate;

/**
 * Represents a student's enrolment in a course.
 * Using a record for immutability — fields cannot be changed after construction.
 */
public record Enrollment(
        String enrollmentId,
        String studentId,
        String studentName,
        String courseId,
        String courseName,
        double completionPercentage,
        LocalDate enrolledOn
) {
    public Enrollment {
        if (completionPercentage < 0.0 || completionPercentage > 100.0) {
            throw new IllegalArgumentException("Completion percentage must be between 0 and 100.");
        }
    }

    /** Returns a new Enrollment with an updated completion percentage (immutable update). */
    public Enrollment withCompletionPercentage(double newPercentage) {
        return new Enrollment(enrollmentId, studentId, studentName, courseId, courseName, newPercentage, enrolledOn);
    }
}
